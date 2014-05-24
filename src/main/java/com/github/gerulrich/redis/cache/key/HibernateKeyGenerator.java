package com.github.gerulrich.redis.cache.key;

import java.io.Serializable;

import org.hibernate.cache.CacheKey;

public abstract class HibernateKeyGenerator extends AbstractKeyGenerator {

	@Override
	protected String transformKeyObject(Object key) {
		CacheKey cacheKey = (CacheKey) key;
		return generateKey(cacheKey.getKey());
	}
	
	public abstract String generateKey(Serializable key);

}
