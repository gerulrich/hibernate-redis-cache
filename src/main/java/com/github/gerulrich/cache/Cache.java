package com.github.gerulrich.cache;

import org.hibernate.cache.CacheException;

public interface Cache {

    String getName();

    Object get(Object key) throws CacheException;

    void put(Object key, Object value) throws CacheException;

    void remove(Object key) throws CacheException;

    boolean isKeyInCache(Object key) throws CacheException;

    void removeAll();
}
