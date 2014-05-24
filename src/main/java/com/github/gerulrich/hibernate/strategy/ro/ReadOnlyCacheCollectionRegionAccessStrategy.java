package com.github.gerulrich.hibernate.strategy.ro;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.hibernate.regions.CacheCollectionRegion;
import com.github.gerulrich.hibernate.strategy.AbstractCacheAccessStrategy;

/**
 * read-only collection region access strategy
 * 
 * @author German Ulrich
 */
public class ReadOnlyCacheCollectionRegionAccessStrategy
    extends AbstractCacheAccessStrategy<CacheCollectionRegion>
    implements CollectionRegionAccessStrategy {

    /**
     * Create a read-only access strategy accessing the given collection region.
     */
    public ReadOnlyCacheCollectionRegionAccessStrategy(CacheCollectionRegion region, Settings settings) {
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
     * Throws UnsupportedOperationException since this cache is read-only
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    /**
     * A no-op since this cache is read-only
     */
    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // throw new UnsupportedOperationException("Can't write to a readonly object");
    }
}
