package com.github.gerulrich.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.redis.cache.RedisCacheImpl;

public class ClearIndexSubscriber
    extends JedisPubSub {

    private static final Logger logger = LoggerFactory.getLogger(ClearIndexSubscriber.class);
    private Map<String, Cache> caches;

    public ClearIndexSubscriber(Map<String, Cache> caches) {
        this.caches = caches;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("PubSub message received, channel {}, message {}", channel, message);
        }
        Cache cache = this.caches.get(message);
        if (cache != null) {
            ((RedisCacheImpl) cache).updateClearIndex();
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    }

}
