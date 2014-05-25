package com.github.gerulrich.redis.cache.key;

public abstract class DigestKeyGenerator
    extends AbstractKeyGenerator {

    @Override
    protected String transformKeyObject(Object key) {
        return key.toString() + ":" + key.hashCode();
    }

    @Override
    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        String longKey = super.concatenateKey(regionName, clearIndex, key);
        return this.digest(longKey);
    }

    protected abstract String digest(String string);
}
