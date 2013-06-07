package nl.runnable.alfresco.policy.samples;

import nl.runnable.alfresco.behaviours.annotations.AssociationPolicy;
import nl.runnable.alfresco.behaviours.annotations.Behaviour;
import nl.runnable.alfresco.behaviours.annotations.Event;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.service.cmr.repository.AssociationRef;

/**
 * Association policy with an explicit association class set on the {@link @AssociationPolicy}.
 *
 * @author Laurent Van der Linden
 */
@Behaviour("cm:content")
public class AssociationBehaviourContain implements NodeServicePolicies.OnCreateAssociationPolicy {
	@AssociationPolicy(value = "cm:folder", association = "cm:contains", event = Event.COMMIT)
	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {}
}
