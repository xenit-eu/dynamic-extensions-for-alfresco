package com.github.dynamicextensionsalfresco.osgi;

/**
 * Strategy for system package cache.
 *
 * @author Laurent Van der Linden
 */
public enum PackageCacheMode {
	ENABLE(true, true, false), DISABLE(false, false, false), UPDATE(false, true, true);

	private final boolean readFromCache;
	private final boolean writeToCache;
	private final boolean forceWriteToCache;

	PackageCacheMode(boolean readFromCache, boolean writeToCache, boolean forceWriteToCache) {
		this.readFromCache = readFromCache;
		this.writeToCache = writeToCache;
		this.forceWriteToCache = forceWriteToCache;
	}

	public boolean isReadFromCache() {
		return readFromCache;
	}

	public boolean isWriteToCache() {
		return writeToCache;
	}

	public boolean isForceWriteToCache() {
		return forceWriteToCache;
	}
}
