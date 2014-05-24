package com.github.gerulrich.hibernate.regions;

import java.util.Properties;

import org.hibernate.cache.QueryResultsRegion;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.hibernate.strategy.AccessStrategyFactory;

/**
 * A query results region wrapper around an Cache instance.
 * 
 * @author German Ulrich
 */
public class CacheQueryResultsRegion
    extends CacheGeneralDataRegion
    implements QueryResultsRegion {

    public CacheQueryResultsRegion(AccessStrategyFactory accessStrategyFactory, Cache underlyingCache, Properties properties) {
        super(accessStrategyFactory, underlyingCache, properties);
    }
}
