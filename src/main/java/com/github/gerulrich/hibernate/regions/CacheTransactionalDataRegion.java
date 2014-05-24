package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

/**
 * An Cache specific TransactionalDataRegion.
 *
 * This is the common superclass entity and collection regions.
 * 
 * @author German Ulrich
 */
public class CacheTransactionalDataRegion
    extends CacheDataRegion
    implements TransactionalDataRegion {

    /**
     * Hibernate settings associated with the persistence unit.
     */
    protected final Settings settings;

    /**
     * Metadata associated with the objects stored in the region.
     */
    protected final CacheDataDescription metadata;

    public CacheTransactionalDataRegion(AccessStrategyFactory accessStrategyFactory, Cache cache, Settings settings,
        CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory, cache, properties);
        this.settings = settings;
        this.metadata = metadata;
    }

    /**
     * Return the hibernate settings
     * 
     * @return settings
     */
    public Settings getSettings() {
        return this.settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransactionAware() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheDataDescription getCacheDataDescription() {
        return this.metadata;
    }

    /**
     * Get the value mapped to this key, or null if no value is mapped to this key.
     */
    public final Object get(Object key) {
        return this.cache.get(key);
    }

    /**
     * Map the given value to the given key, replacing any existing mapping for
     * this key
     */
    public final void put(Object key, Object value) throws CacheException {
        this.cache.put(key, value);
    }

    /**
     * Remove the mapping for this key (if any exists).
     */
    public final void remove(Object key) throws CacheException {
        this.cache.remove(key);
    }

    /**
     * Remove all mapping from this cache region.
     */
    public final void clear() throws CacheException {
        this.cache.removeAll();

    }
}
