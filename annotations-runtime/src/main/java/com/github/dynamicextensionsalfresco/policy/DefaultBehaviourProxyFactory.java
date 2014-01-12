package com.github.dynamicextensionsalfresco.policy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.policy.Behaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Default {@link BehaviourProxyFactory} implementation intended for use in a Spring application context. This
 * implementation releases references to {@link Behaviour}s when the {@link ContextClosedEvent} occurs.
 * 
 * @author Laurens Fridael
 * 
 */
public class DefaultBehaviourProxyFactory implements BehaviourProxyFactory, ApplicationListener<ContextClosedEvent> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* State */

	private final List<BehaviourProxy> behaviourProxies = new ArrayList<BehaviourProxy>();

	/* Main operations */

	@Override
	public BehaviourProxy createBehaviourProxy(final Behaviour behaviour) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating BehaviourProxy for {} instance.", behaviour.getClass().getName());
		}
		final BehaviourProxy behaviourProxy = new BehaviourProxy(behaviour);
		behaviourProxies.add(behaviourProxy);
		return behaviourProxy;
	}

	/* Callback operations */

	@Override
	public void onApplicationEvent(final ContextClosedEvent event) {
		releaseBehaviourReferences();
	}

	/* Utility operations */

	protected void releaseBehaviourReferences() {
		for (final Iterator<BehaviourProxy> it = behaviourProxies.iterator(); it.hasNext();) {
			try {
				final BehaviourProxy behaviourProxy = it.next();
				if (logger.isDebugEnabled()) {
					logger.debug("Releasing reference from BehaviourProxy to {} instance.", behaviourProxy
							.getBehaviour().getClass().getName());
				}
				behaviourProxy.release();
			} finally {
				it.remove();
			}
		}
	}

}
