package com.github.dynamicextensionsalfresco.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.PolicyComponent;

/**
 * {@link InvocationHandler} for {@link PolicyComponent} operations that replaces {@link Behaviour} arguments in method
 * invocations with a {@link BehaviourProxy}.
 * <p>
 * The reason we don't implement {@link PolicyComponent} directly, is because the {@link PolicyComponent} interface has
 * references to package-level classes and interfaces in the <code>org.alfresco.repo.policy</code> package.
 * 
 * @author Laurens Fridael
 * 
 */
class ProxyPolicyComponentInvocationHandler implements InvocationHandler {

	private final PolicyComponent policyComponent;

	private final BehaviourProxyFactory behaviourProxyFactory;

	ProxyPolicyComponentInvocationHandler(final PolicyComponent policyComponent,
			final BehaviourProxyFactory behaviourProxyFactory) {
		this.policyComponent = policyComponent;
		this.behaviourProxyFactory = behaviourProxyFactory;
	}

	@Override
	public Object invoke(final Object object, final Method method, final Object[] args) throws Throwable {
		if (isBehaviourBindingMethod(method)) {
			replaceBehaviourArgumentsWithProxies(args);
		}
		return method.invoke(policyComponent, args);
	}

	private boolean isBehaviourBindingMethod(final Method method) {
		return method.getName().matches("bind\\w+?Behaviour");
	}

	private void replaceBehaviourArgumentsWithProxies(final Object[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Behaviour) {
				args[i] = behaviourProxyFactory.createBehaviourProxy((Behaviour) args[i]);
			}
		}
	}

}
