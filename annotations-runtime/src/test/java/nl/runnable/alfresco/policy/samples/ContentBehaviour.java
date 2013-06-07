package nl.runnable.alfresco.policy.samples;

import nl.runnable.alfresco.behaviours.annotations.Behaviour;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.service.cmr.repository.ChildAssociationRef;

/**
 * Basic class level node policy.
 *
 * @author Laurent Van der Linden
 */
@Behaviour("cm:content")
public class ContentBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
	@Override
	public void onCreateNode(ChildAssociationRef childAssociationRef) {}
}
