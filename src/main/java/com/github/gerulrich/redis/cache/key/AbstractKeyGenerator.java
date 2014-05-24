package com.github.gerulrich.redis.cache.key;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKeyGenerator
    implements KeyGenerator {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");

    public String toKey(String regionName, long clearIndex, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        String keyString = this.concatenateKey(regionName, clearIndex, this.transformKeyObject(key));
        String finalKey = CLEAN_PATTERN.matcher(keyString).replaceAll("");
        this.log.debug("Final cache key: [{}]", finalKey);
        return finalKey;
    }

    protected abstract String transformKeyObject(Object key);

    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        return new StringBuilder().append(regionName).append(":").append(clearIndex).append(":").append(String.valueOf(key))
            .toString();
    }
}
