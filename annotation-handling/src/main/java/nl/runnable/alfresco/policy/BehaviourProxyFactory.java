package nl.runnable.alfresco.policy;

import org.alfresco.repo.policy.Behaviour;

/**
 * Defines factory operations for creating {@link BehaviourProxy} instances.
 * 
 * @author Laurens Fridael
 * 
 */
public interface BehaviourProxyFactory {

	BehaviourProxy createBehaviourProxy(Behaviour behaviour);

}
