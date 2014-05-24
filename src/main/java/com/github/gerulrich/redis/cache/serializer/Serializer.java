package com.github.gerulrich.redis.cache.serializer;

public interface Serializer {
	
	 /**
     * Serialize Object
     */
    byte[] serialize(final Object object);

    /**
     * Deserialize to object
     */
    Object deserialize(final byte[] bytes);

}
