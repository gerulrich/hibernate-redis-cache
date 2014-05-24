package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.GeneralDataRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;


/**
 * An Cache based GeneralDataRegion.
 * 
 * @author German Ulrich
 */
public abstract class CacheGeneralDataRegion
    extends CacheDataRegion
    implements GeneralDataRegion {

    private static final Logger logger = LoggerFactory.getLogger(CacheGeneralDataRegion.class);

    /**
     * Creates an CacheGeneralDataRegion using the given Cache instance as a backing.
     */
    public CacheGeneralDataRegion(AccessStrategyFactory accessStrategyFactory, Cache cache, Properties properties) {
        super(accessStrategyFactory, cache, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) throws CacheException {
        logger.debug("Get object from cache with key: {}", key);
        if (key == null) {
            return null;
        } else {
            return this.cache.get(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Object key, Object value) throws CacheException {
        logger.debug("Put object to cache with key: {} and value: {}", key, value);
        this.cache.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(Object key) throws CacheException {
        logger.debug("Remove object from cache with key: {}", key);
        this.cache.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictAll() throws CacheException {
        this.cache.removeAll();
    }
}
