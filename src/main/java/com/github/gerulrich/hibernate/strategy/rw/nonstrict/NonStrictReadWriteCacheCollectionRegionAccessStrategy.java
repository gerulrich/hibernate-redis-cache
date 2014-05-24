package com.github.gerulrich.hibernate.strategy.rw.nonstrict;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.hibernate.regions.CacheCollectionRegion;
import com.github.gerulrich.hibernate.strategy.AbstractCacheAccessStrategy;

/**
 * non-strict read/write collection region access strategy
 * 
 * @author German Ulrich
 */
public class NonStrictReadWriteCacheCollectionRegionAccessStrategy
    extends AbstractCacheAccessStrategy<CacheCollectionRegion>
    implements CollectionRegionAccessStrategy {

    public NonStrictReadWriteCacheCollectionRegionAccessStrategy(CacheCollectionRegion region, Settings settings) {
        super(region, settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionRegion getRegion() {
        return this.region;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return this.region.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
        throws CacheException {
        if (minimalPutOverride && this.region.contains(key)) {
            return false;
        } else {
            this.region.put(key, value);
            return true;
        }
    }

    /**
     * Since this is a non-strict read/write strategy item locking is not used.
     */
    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    /**
     * Since this is a non-strict read/write strategy item locking is not used.
     */
    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        this.region.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Object key) throws CacheException {
        this.region.remove(key);
    }
}
