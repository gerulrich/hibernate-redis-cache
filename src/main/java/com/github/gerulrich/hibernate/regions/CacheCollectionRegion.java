package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

public class CacheCollectionRegion
    extends CacheTransactionalDataRegion
    implements CollectionRegion {

    /**
     * Constructs an CacheCollectionRegion around the given underlying cache.
     *
     * @param accessStrategyFactory
     */
    public CacheCollectionRegion(AccessStrategyFactory accessStrategyFactory, Cache underlyingCache, Settings settings,
        CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory, underlyingCache, settings, metadata, properties);
    }

    /**
     * {@inheritDoc}
     */
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return this.accessStrategyFactory.createCollectionRegionAccessStrategy(this, accessType);
    }
}
