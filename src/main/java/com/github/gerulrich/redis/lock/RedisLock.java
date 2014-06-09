package com.github.gerulrich.redis.lock;

import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

import redis.clients.jedis.exceptions.JedisException;

public class RedisLock {
	
	private RedisClient client;
	private String key;
	private String uuid;
	private long lockTimeout;
	private boolean locked;
	
	public RedisLock(RedisClient client, String key, long lockTimeout) {
		super();
		this.client = client;
		this.key = key;
		this.uuid = UUID.randomUUID().toString();
		this.lockTimeout = lockTimeout;
		this.locked = false;
	}
	
	public boolean lock(long timeout) {
		try {
			long now = System.currentTimeMillis();
			long max = now+timeout;
			for(; !locked && now <= max; now = System.currentTimeMillis()) {
				int result = Integer.decode(client.set(key, uuid, "NX", "EX", this.lockTimeout));
				locked = (result == 1);
				LockSupport.parkNanos(50 * 1000000);
			}
		} catch (JedisException e) {
			// TODO			
			return false;
		}

		return locked;
	}
	
	public void unlock() {
		if (locked) {
			try {
				client.del(key, uuid);
			} catch (JedisException e) {
				// TODO
			}
		}
	}
}
