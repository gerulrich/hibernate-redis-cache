package com.github.gerulrich.redis.cache.key;

public interface KeyGenerator {

    String toKey(String regionName, long clearIndex, Object key);

}
