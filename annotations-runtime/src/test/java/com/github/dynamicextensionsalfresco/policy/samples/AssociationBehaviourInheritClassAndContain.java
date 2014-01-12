package nl.runnable.alfresco.policy.samples;

import nl.runnable.alfresco.behaviours.annotations.AssociationPolicy;
import nl.runnable.alfresco.behaviours.annotations.Behaviour;
import nl.runnable.alfresco.behaviours.annotations.Event;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.service.cmr.repository.AssociationRef;

/**
 * Association policy that should inherit it's class from the {@link Behaviour} annotation.
 *
 * @author Laurent Van der Linden
 */
@Behaviour("cm:content")
public class AssociationBehaviourInheritClassAndContain implements NodeServicePolicies.OnCreateAssociationPolicy {
	@AssociationPolicy(association = "cm:contains", event = Event.FIRST)
	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {}
}
