package com.github.dynamicextensionsalfresco.policy.samples;

import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.service.cmr.repository.AssociationRef;

/**
 * Association policy without any filter.
 *
 * @author Laurent Van der Linden
 */
@Behaviour
public class AssociationBehaviourVoid implements NodeServicePolicies.OnCreateAssociationPolicy {
	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {}
}
