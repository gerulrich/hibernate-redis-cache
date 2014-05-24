package com.github.gerulrich.redis.cache.key;

import java.io.Serializable;

public class ToStringKeyStrategy extends HibernateKeyStragety {

	@Override
	public String generateKey(Serializable key) {
		return key.toString();
	}
}
