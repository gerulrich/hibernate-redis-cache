package com.github.gerulrich.hibernate;

import java.util.Properties;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.QueryResultsRegion;
import org.hibernate.cache.RegionFactory;
import org.hibernate.cache.TimestampsRegion;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.regions.CacheCollectionRegion;
import com.github.gerulrich.hibernate.regions.CacheEntityRegion;
import com.github.gerulrich.hibernate.regions.CacheQueryResultsRegion;
import com.github.gerulrich.hibernate.regions.CacheTimestampsRegion;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

/**
 * Abstract implementation of RegionFactory.
 * 
 * @author German Ulrich
 */
public abstract class AbstractCacheRegionFactory
    implements RegionFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheRegionFactory.class);

    /**
     * Settings object for the Hibernate persistence unit.
     */
    protected Settings settings;

    /**
     * {@link AccessStrategyFactory} for creating various access strategies
     */
    protected final AccessStrategyFactory accessStrategyFactory;

    public AbstractCacheRegionFactory(AccessStrategyFactory accessStrategyFactory) {
        this.accessStrategyFactory = accessStrategyFactory;
    }

    protected abstract Cache getCache(String name) throws CacheException;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
        throws CacheException {
        if (logger.isDebugEnabled()) {
            logger.debug("Building entity region {}", regionName);
        }
        return new CacheEntityRegion(this.accessStrategyFactory, this.getCache(regionName), this.settings, metadata,
            properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
        throws CacheException {
        return new CacheCollectionRegion(this.accessStrategyFactory, this.getCache(regionName), this.settings, metadata,
            properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new CacheQueryResultsRegion(this.accessStrategyFactory, this.getCache(regionName), properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new CacheTimestampsRegion(this.accessStrategyFactory, this.getCache(regionName), properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.NONSTRICT_READ_WRITE;
    }

}
