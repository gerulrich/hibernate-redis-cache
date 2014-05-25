package com.github.gerulrich.hibernate.regions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.Region;
import org.hibernate.cache.Timestamper;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

/**
 * An Cache data region implementation.
 *
 * @author German Ulrich
 */
public abstract class CacheDataRegion
    implements Region {


    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "com.github.gerulrich.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60000;

    /**
     * Cache instance backing this Hibernate data region.
     */
    protected final Cache cache;

    /**
     * The {@link AccessStrategyFactory} used for creating various access strategies
     */
    protected final AccessStrategyFactory accessStrategyFactory;

    private final int lockTimeout;

    public CacheDataRegion(AccessStrategyFactory accessStrategyFactory, Cache cache, Properties properties) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.cache = cache;
        String timeout = properties.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY, Integer.toString(DEFAULT_CACHE_LOCK_TIMEOUT));
        this.lockTimeout = Integer.decode(timeout);
    }

    /**
     * Return the Cache instance backing this Hibernate data region.
     */
    public Cache getCache() {
        return this.cache;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.cache.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws CacheException {
        // TODO destroy cache
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSizeInMemory() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getElementCountInMemory() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map toMap() {
        return new HashMap<Object, Object>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTimeout() {
        return this.lockTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object key) {
        return this.cache.isKeyInCache(key);
    }
}
