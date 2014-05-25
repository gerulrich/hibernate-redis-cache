package com.github.gerulrich.hibernate.strategy;

import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.EntityRegionAccessStrategy;

import com.github.gerulrich.hibernate.regions.CacheCollectionRegion;
import com.github.gerulrich.hibernate.regions.CacheEntityRegion;
import com.github.gerulrich.hibernate.timestamper.Timestamper;

/**
 * Factory to create {@link EntityRegionAccessStrategy}
 * 
 * @author German Ulrich
 * 
 */
public interface AccessStrategyFactory {

    /**
     * Create {@link EntityRegionAccessStrategy} for the input
     * {@link CacheEntityRegion} and {@link AccessType}
     * 
     * @param entityRegion
     * @param accessType
     * @return the created {@link EntityRegionAccessStrategy}
     */
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(CacheEntityRegion entityRegion, AccessType accessType);

    /**
     * Create {@link CollectionRegionAccessStrategy} for the input
     * {@link CacheCollectionRegion} and {@link AccessType}
     * 
     * @param collectionRegion
     * @param accessType
     * @return the created {@link CollectionRegionAccessStrategy}
     */
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(CacheCollectionRegion collectionRegion,
        AccessType accessType);

    public Timestamper getTimestamper();

}
