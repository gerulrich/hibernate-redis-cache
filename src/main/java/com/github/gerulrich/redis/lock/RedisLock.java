package com.github.gerulrich.redis.lock;

import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

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
		long now = System.currentTimeMillis();
		long max = now+timeout;
		for(; !locked && now <= max; LockSupport.parkNanos(50 * 1000000), now = System.currentTimeMillis()) {
			locked = client.setLock(key, uuid, lockTimeout);
		}
		
		return locked;
	}
	
	public void unlock() {
		if (locked) {
			client.delLock(key, uuid);
		}
	}
}
