package com.github.gerulrich.redis.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandarSerializer
    implements Serializer {

    private static Logger logger = LoggerFactory.getLogger(StandarSerializer.class);

    public byte[] serialize(final Object object) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize object", e);
        } finally {
            this.close(bos);
        }
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize object", e);
        } finally {
            this.close(bis);
            this.close(in);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error("Failed to close closeable.", e);
            }
        }
    }

    private void close(ObjectInput input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                logger.error("Failed to close Object input.", e);
            }
        }
    }

}
