package com.github.dynamicextensionsalfresco.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.alfresco.repo.policy.PolicyComponent;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link FactoryBean} for creating proxy {@link PolicyComponent}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class ProxyPolicyComponentFactoryBean implements FactoryBean<PolicyComponent> {

	/* Dependencies */

	private PolicyComponent policyComponent;

	private BehaviourProxyFactory behaviourProxyFactory;

	/* State */

	private PolicyComponent proxyPolicyComponent;

	/* Operations */

	@Override
	public Class<? extends PolicyComponent> getObjectType() {
		return PolicyComponent.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public PolicyComponent getObject() {
		if (proxyPolicyComponent == null) {
			proxyPolicyComponent = createProxyPolicyComponent();
		}
		return proxyPolicyComponent;
	}

	/* Utility operations */

	protected PolicyComponent createProxyPolicyComponent() {
		final InvocationHandler handler = new ProxyPolicyComponentInvocationHandler(getPolicyComponent(),
				getBehaviourProxyFactory());
		return (PolicyComponent) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { PolicyComponent.class }, handler);
	}

	/* Dependencies */

	@Required
	public void setPolicyComponent(final PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	protected PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	@Required
	public void setBehaviourProxyFactory(final BehaviourProxyFactory behaviourProxyFactory) {
		this.behaviourProxyFactory = behaviourProxyFactory;
	}

	protected BehaviourProxyFactory getBehaviourProxyFactory() {
		return behaviourProxyFactory;
	}

}
