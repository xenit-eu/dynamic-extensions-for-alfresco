package com.github.dynamicextensionsalfresco.proxy;

/**
 * Service visitor that gets a reference to a service for the duration of the operation.
 * No reference to the service should be kept.
 *
 * @author Laurent Van der Linden
 */
public interface ServiceInvoker<T> {
	Object invokeService(T service) throws Throwable;
}
