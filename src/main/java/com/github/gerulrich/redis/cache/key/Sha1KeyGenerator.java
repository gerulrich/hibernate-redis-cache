package com.github.gerulrich.redis.cache.key;


public class Sha1KeyGenerator
    extends DigestKeyGenerator {

    @Override
    protected String digest(String key) {
        return HashHelper.sha1Hex(key);
    }
}
