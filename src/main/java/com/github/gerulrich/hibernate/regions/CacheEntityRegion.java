package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

public class CacheEntityRegion
    extends CacheTransactionalDataRegion
    implements EntityRegion {

    public CacheEntityRegion(AccessStrategyFactory accessStrategyFactory, Cache underlyingCache, Settings settings,
        CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory, underlyingCache, settings, metadata, properties);
    }

    /**
     * {@inheritDoc}
     */
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return this.accessStrategyFactory.createEntityRegionAccessStrategy(this, accessType);
    }

}
