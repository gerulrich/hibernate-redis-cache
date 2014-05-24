package com.github.gerulrich.hibernate.strategy.ro;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.hibernate.regions.CacheEntityRegion;
import com.github.gerulrich.hibernate.strategy.AbstractCacheAccessStrategy;

public class ReadOnlyCacheEntityRegionAccessStrategy
    extends AbstractCacheAccessStrategy<CacheEntityRegion>
    implements EntityRegionAccessStrategy {

    public ReadOnlyCacheEntityRegionAccessStrategy(CacheEntityRegion entityRegion, Settings settings) {
        super(entityRegion, settings);
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
     * Throws UnsupportedOperationException since this cache is read-only
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public SoftLock lockItem(Object key, Object version) {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    /**
     * A no-op since this cache is read-only
     */
    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    /**
     * This cache is asynchronous hence a no-op
     */
    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        this.region.put(key, value);
        return true;
    }

    /**
     * Throws UnsupportedOperationException since this cache is read-only
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    /**
     * Throws UnsupportedOperationException since this cache is read-only
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

}
