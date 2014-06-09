package com.github.gerulrich.redis.cache;

import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.redis.Constants;
import com.github.gerulrich.redis.cache.key.KeyGenerator;
import com.github.gerulrich.redis.cache.serializer.SerializationException;
import com.github.gerulrich.redis.cache.serializer.Serializer;
import com.github.gerulrich.redis.lock.RedisClient;

/**
 * Cache implementation based on redis. 
 * @author German Ulrich
 *
 */
public class RedisCacheImpl
    implements Cache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheImpl.class);

    private RedisClient client;
    private String name;
    private String preffix;
    private KeyGenerator keyGenerator;
    private Serializer serializer;
    private boolean clustered;
    private int clearIndex;
    private int ttl;

    public RedisCacheImpl(String name, String preffix, JedisPool jedisPool, KeyGenerator keyGenerator,
        Serializer serializer, int clearIndex, int ttl) {
        this(name, preffix, jedisPool, keyGenerator, serializer, clearIndex, ttl, false);
    }

    public RedisCacheImpl(String name, String preffix, JedisPool jedisPool, KeyGenerator keyGenerator,
        Serializer serializer, int clearIndex, int ttl, boolean clustered) {
        super();
        this.name = name;
        this.preffix = preffix;
        this.client = new RedisClient(jedisPool);
        this.keyGenerator = keyGenerator;
        this.serializer = serializer;
        this.clearIndex = clearIndex;
        this.ttl = ttl;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object get(Object key) throws CacheException {
        try {
        	String objectKey = this.getKey(key);
        	if (logger.isDebugEnabled()) {
                logger.trace("Getting object from cache [{}] for key [{}]", this.getName(), objectKey);
            }
            byte bytes[] = client.get(objectKey.getBytes());
            if (bytes == null) {
            	 if (logger.isDebugEnabled()) {
            		 logger.debug("Value for key [{}] is null.", objectKey);
                 }
                return null;
            }
            return this.serializer.deserialize(bytes);
        } catch (JedisException e) {
        	throw new CacheException("Failed to get object from cache", e);
        } catch (SerializationException e) {
        	 throw new CacheException("Failed to deserialize cached object", e);
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        try {
            String objectKey = this.getKey(key);
            if (logger.isDebugEnabled()) {
                logger.debug("Putting object in cache [{}] for key [{}]", this.getName(), objectKey);
            }
            byte objectValue[] = this.serializer.serialize(value);
            client.setex(objectKey.getBytes(), this.ttl, objectValue);
        } catch (JedisException e) {
        	throw new CacheException("Failed to put object in cache", e);
        } catch (SerializationException e) {
        	 throw new CacheException("Failed to serialize object", e);
        }
    }

    @Override
    public void remove(Object key) throws CacheException {
    	try {
    		String objectKey = this.getKey(key);
    		if (logger.isDebugEnabled()) {
                logger.debug("Removing object from cache [{}] for key [{}]", this.getName(), objectKey);
            }
            client.del(objectKey.getBytes());
    	} catch (JedisException e) {
        	throw new CacheException("Failed to remove object from cache", e);
    	}    	
    }

    @Override
    public boolean isKeyInCache(Object key) {
    	try {
            String objectKey = this.getKey(key);
            if (logger.isDebugEnabled()) {
                logger.debug("Checking if key exists in the cache [{}]", objectKey);
            }
            return client.exists(objectKey);
    	} catch (JedisException e) {
        	throw new CacheException("Failed to remove object from the cache", e);
    	}
    }

    @Override
    public void removeAll() {
        if (logger.isDebugEnabled()) {
            logger.debug("Clearing all objects from cache [{}]", this.getName());
        }
        try {
        	Long value = client.hincrBy(getClearIndexKey(), this.getName(), 1L);
        	this.clearIndex = value.intValue();
        	if (this.clustered) {
                client.publish(this.preffix + Constants.UPDATE_CLEAR_INDEX_KEY, this.getName());
            }
        } catch (JedisException e) {
        	throw new CacheException("Failed to remove all objects from the cache", e);
        }
    }

    public void updateClearIndex() {
    	try {
    		this.clearIndex = Integer.decode(client.hget(getClearIndexKey(), this.getName()));
    	} catch (JedisException e) {
    		throw new CacheException(e);
    	}
    }

	private String getClearIndexKey() {
		return this.preffix + Constants.CLEAR_INDEX_KEY;
	}

    private String getKey(Object key) {
        String redisKey = this.keyGenerator.toKey(this.getName(), this.clearIndex, key);
        if (this.preffix != null && !this.preffix.isEmpty()) {
            return this.preffix + redisKey;
        }
        return redisKey;
    }
}