package com.github.gerulrich.redis.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class StandarSerializer implements Serializer {

	public byte[] serialize(final Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			return bos.toByteArray();
		} catch (IOException e) {
			// TODO throw exception
		} finally {
			this.close(bos);
		}
		return null;
	}

	public Object deserialize(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			return o;
		} catch (Exception e) {
			// TODO
		} finally {
			this.close(bis);
			this.close(in);
		}
		return null;
	}
	
	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void close(ObjectInput input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
