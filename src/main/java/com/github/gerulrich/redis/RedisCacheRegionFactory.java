package com.github.gerulrich.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;
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
    private static final int DEFAULT_LOCK_TIMEOUT = 30000;
    private static final String DEFAULT_KEY_GENERATOR = "com.github.gerulrich.redis.cache.key.StringKeyGenerator";
    private static final String DEFAULT_SERIALIZER = "com.github.gerulrich.redis.cache.serializer.StandarSerializer";

    private JedisPool jedisPool;
    private Properties properties;
    private Map<String, Cache> caches;

    private long ttl = DEFAULT_CACHE_TTL;
    private long lockTimeout = DEFAULT_LOCK_TIMEOUT;
    private String keyGenerator = DEFAULT_KEY_GENERATOR;
    private String serializer = DEFAULT_SERIALIZER;
    private String preffix = "redis:";
    private boolean clustered = false;
    private ClearIndexSubscriber subscriber;
    private Jedis jedisSubscriber;

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
        this.properties.put(Constants.CACHE_LOCK_TIMEOUT_PROPERTY, this.getLockTimeout());
        if (this.clustered) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RedisCacheRegionFactory.this.startSubscription();
                }
            }, "PubSub-" + this.getPreffixKey() + Constants.CLEAR_INDEX_KEY).start();
        }
    }

    private void startSubscription() {
        String channel = this.getPreffixKey() + Constants.CLEAR_INDEX_KEY;
        logger.debug("Starting subcribtion for channel {}", channel);
        this.subscriber = new ClearIndexSubscriber(this.caches);
        this.jedisSubscriber = this.jedisPool.getResource();
        this.jedisSubscriber.subscribe(this.subscriber, channel);
    }

    @Override
    public void stop() {
        this.stopSubscription();
        // TODO
        this.caches.clear();
    }
    
    private void stopSubscription() {
    	if (this.clustered && this.subscriber != null) {
    		String channel = this.getPreffixKey() + Constants.CLEAR_INDEX_KEY;
    		logger.debug("Stopping subcribtion for channel {}", channel);
    		this.subscriber.unsubscribe(this.getPreffixKey() + Constants.CLEAR_INDEX_KEY);
    		this.jedisPool.returnResource(this.jedisSubscriber);
    	}
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    protected Cache getCache(String name) throws CacheException {
        if (!this.caches.containsKey(name)) {
            int ttl = this.getTTL(name);
            int clearIndex = this.getClearIndex(name);
            String preffix = this.getPreffixKey();
            KeyGenerator keyGenerator = this.getKeyGenerator(name);
            Serializer serializer = this.getSerializer(name);

            Cache cache = new RedisCacheImpl(name, preffix, this.jedisPool, keyGenerator, serializer, clearIndex, ttl,
                this.clustered);

            if (logger.isDebugEnabled()) {
                Object params[] = {
                    name, preffix, clearIndex, ttl, this.getClasName(keyGenerator), this.getClasName(serializer)};
                logger
                    .debug(
                        "Creating redis cache region for {}, preffix: {}, clear index: {}, ttl: {}, key generator: {}, serializer: {}",
                        params);
            }

            this.caches.put(name, cache);
        }
        return this.caches.get(name);
    }

    private int getClearIndex(String name) {
        Jedis jedis = this.jedisPool.getResource();
        int clearIndex = 0;
        try {
            String clearIndexKey = this.getPreffixKey() + Constants.CLEAR_INDEX_KEY;
            if (jedis.hexists(clearIndexKey, name)) {
                clearIndex = Integer.decode(jedis.hget(clearIndexKey, name));
            } else {
                jedis.hset(clearIndexKey, name, Integer.toString(clearIndex));
            }
        } finally {
            this.jedisPool.returnResource(jedis);
        }
        return clearIndex;
    }

    private String getPreffixKey() {
        if (this.preffix == null || this.preffix.trim().isEmpty()) {
            return "redis:";
        } else {
            if (this.preffix.endsWith(":")) {
                return this.preffix;
            } else {
                return this.preffix + ":";
            }
        }
    }

    private String getClasName(Object object) {
        return object.getClass().getSimpleName();
    }

    private int getTTL(String name) {
        String propertyName = name + ".ttl";
        String ttl = this.properties.getProperty(propertyName, Long.toString(this.getTtl()));
        return Integer.decode(ttl);
    }

    private KeyGenerator getKeyGenerator(String name) {
        String propertyName = name + ".key_generator";
        String keyGeneratorClass = this.properties.getProperty(propertyName, this.getKeyGenerator());
        return (KeyGenerator) this.newInstance(keyGeneratorClass);
    }

    private Serializer getSerializer(String name) {
        String propertyName = name + ".serializer";
        String keySerializerClass = this.properties.getProperty(propertyName, this.getSerializer());
        return (Serializer) this.newInstance(keySerializerClass);
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

    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getLockTimeout() {
        return this.lockTimeout;
    }

    public void setLockTimeout(long lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public String getKeyGenerator() {
        return this.keyGenerator;
    }

    public void setKeyGenerator(String keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public String getSerializer() {
        return this.serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getPreffix() {
        return this.preffix;
    }

    public void setPreffix(String preffix) {
        this.preffix = preffix;
    }

    public boolean isClustered() {
        return this.clustered;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

}
