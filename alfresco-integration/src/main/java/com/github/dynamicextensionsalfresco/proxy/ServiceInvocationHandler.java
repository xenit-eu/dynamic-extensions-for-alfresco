package com.github.dynamicextensionsalfresco.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * AOP or JDK Proxy compliant wrapper for the service {@link Tracker}
 *
 * @author Laurent Van der Linden
 */
public class ServiceInvocationHandler implements MethodInterceptor, InvocationHandler {
	private final Tracker tracker;

	public ServiceInvocationHandler(Tracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getDeclaringClass().equals(FilterModel.class)) {
			return method.invoke(tracker.getFilterModel(), args);
		} else {
			return tracker.invokeUsing(new ServiceInvoker<Object>() {
				@Override
				public Object invokeService(Object service) throws Throwable {
					try {
						return method.invoke(service, args);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
				}
			});
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return invoke(null, invocation.getMethod(), invocation.getArguments());
	}
}
