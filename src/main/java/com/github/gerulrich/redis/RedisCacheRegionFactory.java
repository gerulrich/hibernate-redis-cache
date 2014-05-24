package com.github.gerulrich.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.AbstractCacheRegionFactory;
import com.github.gerulrich.hibernate.strategy.CacheAccessStrategyFactoryImpl;
import com.github.gerulrich.redis.cache.RedisCacheImpl;
import com.github.gerulrich.redis.cache.key.KeyGenerator;
import com.github.gerulrich.redis.cache.serializer.Serializer;

public class RedisCacheRegionFactory
    extends AbstractCacheRegionFactory {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheRegionFactory.class);
    private static final int DEFAULT_CACHE_TTL = 120;
    private static final String DEFAULT_KEY_STRATEGY = "com.github.gerulrich.redis.cache.key.ToStringKeyStrategy";
    private static final String DEFAULT_SERIALIZER = "com.github.gerulrich.redis.cache.serializer.StandarSerializer";

    private JedisPool jedisPool;
    private Properties properties;
    private Map<String, Cache> caches;

    private long defaultExpirationTime = DEFAULT_CACHE_TTL;
    private String defaultKeyStrategy = DEFAULT_KEY_STRATEGY;
    private String defaultSerializer = DEFAULT_SERIALIZER;

    public RedisCacheRegionFactory() {
        super(new CacheAccessStrategyFactoryImpl());
        this.properties = new Properties();
        this.caches = new HashMap<String, Cache>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.NONSTRICT_READ_WRITE;
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        this.settings = settings;
        this.properties.putAll(properties);
    }

    @Override
    public void stop() {
        // TODO
        // for(Map.Entry<String,Cache> entry : this.caches.entrySet()) {
        // entry.getValue().removeAll();
        // }
        this.caches.clear();
    }

    @Override
    protected Cache getCache(String name) throws CacheException {
        if (!this.caches.containsKey(name)) {
            int ttl = this.getTTL(name);
            int clearIndex = this.getClearIndex(name);
            KeyGenerator keyStrategy = this.getKeyStrategy(name);
            Serializer serializer = this.getSerializer(name);
            Cache cache = new RedisCacheImpl(name, this.jedisPool, keyStrategy, serializer, clearIndex, ttl);

            if (logger.isDebugEnabled()) {
                Object logParams[] = {
                    name, clearIndex, ttl, keyStrategy.getClass().getSimpleName(), serializer.getClass().getSimpleName()};
                logger.debug(
                    "Creating RedisCache for region {}, clear index: {}, ttl: {}, key strategy: {}, serializer: {}",
                    logParams);
            }

            this.caches.put(name, cache);
        }
        return this.caches.get(name);
    }

    private int getClearIndex(String name) {
        Jedis jedis = this.jedisPool.getResource();
        int clearIndex = 0;
        try {
            if (jedis.hexists(Constants.CLEAR_INDEX_KEY, name)) {
                clearIndex = Integer.decode(jedis.hget(Constants.CLEAR_INDEX_KEY, name));
            } else {
                jedis.hset(Constants.CLEAR_INDEX_KEY, name, Integer.toString(clearIndex));
            }
        } finally {
            this.jedisPool.returnResource(jedis);
        }
        return clearIndex;
    }

    private int getTTL(String name) {
        String propertyName = name + ".ttl";
        String ttl = this.properties.getProperty(propertyName, Long.toString(this.getDefaultExpirationTime()));
        return Integer.decode(ttl);
    }

    private KeyGenerator getKeyStrategy(String name) {
        String propertyName = name + ".key.strategy";
        String keyStrategyClass = this.properties.getProperty(propertyName, this.getDefaultKeyStrategy());
        return (KeyGenerator) this.newInstance(keyStrategyClass);
    }

    private Serializer getSerializer(String name) {
        String propertyName = name + ".serializer";
        String keyStrategyClass = this.properties.getProperty(propertyName, this.getDefaultSerializer());
        return (Serializer) this.newInstance(keyStrategyClass);
    }

    private Object newInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    public long getDefaultExpirationTime() {
        return this.defaultExpirationTime;
    }

    public void setDefaultExpirationTime(long defaultExpirationTime) {
        this.defaultExpirationTime = defaultExpirationTime;
    }

    public String getDefaultKeyStrategy() {
        return this.defaultKeyStrategy;
    }

    public void setDefaultKeyStrategy(String defaultKeyStrategy) {
        this.defaultKeyStrategy = defaultKeyStrategy;
    }

    public String getDefaultSerializer() {
        return this.defaultSerializer;
    }

    public void setDefaultSerializer(String defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }
}
