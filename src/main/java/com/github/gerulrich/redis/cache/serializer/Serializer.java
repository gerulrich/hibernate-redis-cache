package com.github.gerulrich.redis.cache.serializer;

/**
 * Serializes an Object to a byte array for storage.
 * Also allows deserializes a Object from an array of bytes.
 * @author German Ulrich
 *
 */
public interface Serializer {

    /**
    * Serialize Object
    */
    byte[] serialize(final Object object) throws SerializationException;

    /**
     * Deserialize to object
     */
    Object deserialize(final byte[] bytes) throws SerializationException;

}
