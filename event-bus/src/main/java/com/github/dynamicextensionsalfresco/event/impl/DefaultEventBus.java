package com.github.dynamicextensionsalfresco.event.impl;

import com.github.dynamicextensionsalfresco.event.Event;
import com.github.dynamicextensionsalfresco.event.EventBus;
import com.github.dynamicextensionsalfresco.event.EventListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Synchronous implementation that only notifies listeners based on their generic signature.
 *
 * @author Laurent Van der Linden
 */
public class DefaultEventBus implements EventBus {
	private final BundleContext bundleContext;

	public DefaultEventBus(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void publish(T event) {
		try {
			final ServiceReference<?>[] references = bundleContext.getAllServiceReferences(EventListener.class.getName(), null);
			for (ServiceReference<?> reference : references) {
				final EventListener listener = (EventListener) bundleContext.getService(reference);
				final Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
                final Type listenerType;
                if (genericInterfaces[0] instanceof ParameterizedType) {
                    listenerType = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType)listener.getClass().getSuperclass().getGenericInterfaces()[0];
                    listenerType = parameterizedType.getActualTypeArguments()[0];
                }
				if (event.getClass().equals(listenerType)) {
					listener.onEvent(event);
				}
			}
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
