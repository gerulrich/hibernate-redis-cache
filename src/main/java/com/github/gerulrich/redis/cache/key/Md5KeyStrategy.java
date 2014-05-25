package com.github.gerulrich.redis.cache.key;


public class Md5KeyStrategy
    extends DigestKeyGenerator {

    @Override
    protected String digest(String key) {
        return HashHelper.md5Hex(key);
    }
}
