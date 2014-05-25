package com.github.gerulrich.hibernate.strategy.rw.nonstrict;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.hibernate.regions.CacheEntityRegion;
import com.github.gerulrich.hibernate.strategy.AbstractCacheAccessStrategy;

/**
 * non-strict read/write entity region access strategy
 * 
 * @author German Ulrich
 */
public class NonStrictReadWriteCacheEntityRegionAccessStrategy
    extends AbstractCacheAccessStrategy<CacheEntityRegion>
    implements EntityRegionAccessStrategy {

    public NonStrictReadWriteCacheEntityRegionAccessStrategy(CacheEntityRegion region, Settings settings) {
        super(region, settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegion getRegion() {
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
     * Returns <code>false</code> since this is an asynchronous cache access
     * strategy.
     */
    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    /**
     * Returns <code>false</code> since this is a non-strict read/write cache
     * access strategy
     */
    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    /**
     * Removes the entry since this is a non-strict read/write cache strategy.
     */
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        this.remove(key);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
        throws CacheException {
        this.unlockItem(key, lock);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Object key) throws CacheException {
        this.region.remove(key);
    }
}
