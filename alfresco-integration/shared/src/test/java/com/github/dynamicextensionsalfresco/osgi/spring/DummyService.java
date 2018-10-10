package com.github.dynamicextensionsalfresco.osgi.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Laurent Van der Linden
 */
public class DummyService implements Runnable, InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return "Daft result";
	}

	@Override
	public void run() {
	}
}
