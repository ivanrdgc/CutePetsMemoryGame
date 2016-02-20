package com.puyapps.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple pool
 * @param <T>
 */
public class PuyPool<T> {
	public interface PoolObjectFactory<T> {
		public T createObject();
	}

	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;

	public PuyPool(PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(this.maxSize);
	}

	public T newObject() {
		T object = null;

		if (this.freeObjects.size() == 0)
			object = this.factory.createObject();
		else
			object = this.freeObjects.remove(this.freeObjects.size() - 1);

		return object;
	}

	public void free(T object) {
		if (this.freeObjects.size() < this.maxSize) {
			this.freeObjects.add(object);
		}
	}
}