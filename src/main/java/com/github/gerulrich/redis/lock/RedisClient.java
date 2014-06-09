package com.github.gerulrich.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class RedisClient {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);
	private JedisPool jedisPool;

	public RedisClient(JedisPool jedisPool) {
		super();
		this.jedisPool = jedisPool;
	}
	
	public byte[] get(final byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.get(key);
		} catch (JedisException e) {
			logger.error("Failed to exec GET command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public String setex(final byte[] key, final int seconds, final byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setex(key, seconds, value);
		} catch (JedisException e) {
			logger.error("Failed to exec SETEX command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public String set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.set(key, value, nxxx, expx, time);
		} catch (JedisException e) {
			logger.error("Failed to exec SET command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}	
	
	public Long del(final byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (JedisException e) {
			logger.error("Failed to exec DEL command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void del(String key, String value) {
		String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end"; 
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.eval(script, 1, key, value);
		} catch (JedisException e) {
			logger.error("Failed to exec DEL command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}	
	
	public Boolean exists(final String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.exists(key);
		} catch (JedisException e) {
			logger.error("Failed to exec EXISTS command with key {}", key, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public String hget(final String key, final String field) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (JedisException e) {
			logger.error("Failed to exec HGET command with key {} and field {}", new Object[] {key, field}, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public Long hincrBy(final String key, final String field, final long value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hincrBy(key, field, value);
		} catch (JedisException e) {
			logger.error("Failed to exec HINCRBY command with key {} and field {}", new Object[] {field, key }, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public Long publish(final String channel, final String message) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.publish(channel, message);
		} catch (JedisException e) {
			logger.error("Failed to exec PUBLICSH command with key {} and field {}", new Object[] { channel, message }, e);
			jedisPool.returnResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

}