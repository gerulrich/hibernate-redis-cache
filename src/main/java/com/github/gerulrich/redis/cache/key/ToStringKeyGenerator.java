package com.github.gerulrich.redis.cache.key;

import java.io.Serializable;

public class ToStringKeyGenerator extends HibernateKeyGenerator {

	@Override
	public String generateKey(Serializable key) {
		return key.toString();
	}
}
