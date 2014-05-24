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

/**
 * Proxy for RegionFactory injected by spring
 * @author German Ulrich
 *
 */
public class SpringProxyRegionFactory
    implements RegionFactory {

    private static RegionFactory RF;

    public void setRegionFactory(RegionFactory regionFactory) {
        SpringProxyRegionFactory.RF = regionFactory;
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        RF.start(settings, properties);
    }

    @Override
    public void stop() {
        RF.stop();
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return RF.isMinimalPutsEnabledByDefault();
    }

    @Override
    public AccessType getDefaultAccessType() {
        return RF.getDefaultAccessType();
    }

    @Override
    public long nextTimestamp() {
        return RF.nextTimestamp();
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
        throws CacheException {
        return RF.buildEntityRegion(regionName, properties, metadata);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
        throws CacheException {
        return RF.buildCollectionRegion(regionName, properties, metadata);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return RF.buildQueryResultsRegion(regionName, properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return RF.buildTimestampsRegion(regionName, properties);
    }
}
