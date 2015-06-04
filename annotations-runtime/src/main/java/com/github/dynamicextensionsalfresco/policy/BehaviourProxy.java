package com.github.dynamicextensionsalfresco.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.dynamicextensionsalfresco.metrics.Timer;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Policy;
import org.alfresco.repo.policy.PolicyComponent;
import org.springframework.util.Assert;

/**
 * Proxy that allows a {@link Behaviour} to be garbage-collected.
 * <p>
 * This class prevents dangling references to {@link Behaviour} instances when code is undeployed from an OSGi
 * container, as the {@link PolicyComponent} interface offers no means of unregistering {@link Behaviour}s. Dangling
 * references to the {@link BehaviourProxy} itself will continue to exist throughout the lifetime of the Alfresco
 * process, however. There will be a slight memory leak for every time you redeploy a Dynamic Extension that contains a
 * Behaviour. (Further revisions of this class may add the ability to reattach a Behaviour once a module gets updated.)
 * 
 * @author Laurens Fridael
 * 
 */
public class BehaviourProxy implements Behaviour {

	/* State */

	private final Map<Class<?>, ProxyPolicy> proxiesByPolicyClass = new ConcurrentHashMap<Class<?>, ProxyPolicy>();

	private Behaviour behaviour;

	/* Main operations */

	public BehaviourProxy(final Behaviour behaviour) {
		Assert.notNull(behaviour, "Behaviour cannot be null.");
		this.behaviour = behaviour;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getInterface(final Class<T> policy) {
		T proxy;
		if (proxiesByPolicyClass.containsKey(policy)) {
			proxy = (T) proxiesByPolicyClass.get(policy).proxy;
		} else {
			if (behaviour instanceof NoOpBehaviour) {
				final ProxyPolicyInvocationHandler proxyHandler = new ProxyPolicyInvocationHandler(null);
				proxy = (T) Proxy
						.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { policy }, proxyHandler);
				proxiesByPolicyClass.put(policy, new ProxyPolicy(proxy, proxyHandler));
			} else {
				final T originalHandler = behaviour.getInterface(policy);
				final ProxyPolicyInvocationHandler proxyHandler = new ProxyPolicyInvocationHandler(originalHandler);
				proxy = (T) Proxy
						.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { policy }, proxyHandler);
				proxiesByPolicyClass.put(policy, new ProxyPolicy(proxy, proxyHandler));
			}
		}
		return proxy;
	}

	@Override
	public NotificationFrequency getNotificationFrequency() {
		return behaviour.getNotificationFrequency();
	}

	@Override
	public boolean isEnabled() {
		return behaviour.isEnabled();
	}

	@Override
	public void enable() {
		behaviour.enable();
	}

	@Override
	public void disable() {
		behaviour.disable();
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	/**
	 * Clears the reference to the original {@link Behaviour} and clears the target references for the
	 * {@link ProxyPolicyComponentInvocationHandler}s.
	 */
	public synchronized void release() {
		behaviour = new NoOpBehaviour(behaviour.getNotificationFrequency(), behaviour.isEnabled());
		for (final ProxyPolicy proxyPolicy : proxiesByPolicyClass.values()) {
			proxyPolicy.handler.release();
		}
	}

	private static class ProxyPolicyInvocationHandler implements InvocationHandler {

		private Object target;

		private ProxyPolicyInvocationHandler(final Object target) {
			this.target = target;
		}

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			if (method.getDeclaringClass().isAssignableFrom(Object.class)) {
				// Direct Object methods to ourselves.
				return method.invoke(this, args);
			} else if (Policy.class.isAssignableFrom(method.getDeclaringClass())) {
				/* Policy interface operations always return void. */
				if (target != null) {
					Timer.instance.start(target.toString(), args);
					try {
						method.invoke(target, args);
					} finally {
						Timer.instance.stop();
					}
				}
				return null;
			} else {
				/* We should never get to this point. */
				throw new AssertionError("Cannot handle methods from " + method.getDeclaringClass());
			}
		}

		void release() {
			target = null;
		}
	}

	private static class ProxyPolicy {

		final Object proxy;

		final ProxyPolicyInvocationHandler handler;

		private ProxyPolicy(final Object proxy, final ProxyPolicyInvocationHandler handler) {
			this.proxy = proxy;
			this.handler = handler;
		}

	}

}
