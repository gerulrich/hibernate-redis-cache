package com.github.gerulrich.hibernate.strategy;

import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.EntityRegionAccessStrategy;

import com.github.gerulrich.hibernate.regions.CacheCollectionRegion;
import com.github.gerulrich.hibernate.regions.CacheEntityRegion;
import com.github.gerulrich.hibernate.strategy.ro.ReadOnlyCacheCollectionRegionAccessStrategy;
import com.github.gerulrich.hibernate.strategy.ro.ReadOnlyCacheEntityRegionAccessStrategy;
import com.github.gerulrich.hibernate.strategy.rw.nonstrict.NonStrictReadWriteCacheCollectionRegionAccessStrategy;
import com.github.gerulrich.hibernate.strategy.rw.nonstrict.NonStrictReadWriteCacheEntityRegionAccessStrategy;

/**
 * Class implementing {@link AccessStrategyFactory}
 * 
 * @author German Ulrich
 * 
 */
public class CacheAccessStrategyFactoryImpl
    implements AccessStrategyFactory {


    public CacheAccessStrategyFactoryImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(CacheEntityRegion entityRegion, AccessType accessType) {
        if (AccessType.READ_ONLY.equals(accessType)) {
            return new ReadOnlyCacheEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        } else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
            return new NonStrictReadWriteCacheEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        }
        throw new IllegalArgumentException("only support read-only and non-strict-read-write strategies");
    }

    /**
     * {@inheritDoc}
     */
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(CacheCollectionRegion collectionRegion,
        AccessType accessType) {
        if (AccessType.READ_ONLY.equals(accessType)) {
            return new ReadOnlyCacheCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
        } else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
            return new NonStrictReadWriteCacheCollectionRegionAccessStrategy(collectionRegion,
                collectionRegion.getSettings());
        }
        throw new IllegalArgumentException("only support read-only and non-strict-read-write strategies");
    }
}
