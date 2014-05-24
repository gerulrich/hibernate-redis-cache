package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.TimestampsRegion;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

/**
 * A timestamps region specific wrapper around an Cache instance.
 *
 * @author German Ulrich
 */
public class CacheTimestampsRegion
    extends CacheGeneralDataRegion
    implements TimestampsRegion {

    public CacheTimestampsRegion(AccessStrategyFactory accessStrategyFactory, Cache underlyingCache, Properties properties) {
        super(accessStrategyFactory, underlyingCache, properties);
    }
}
