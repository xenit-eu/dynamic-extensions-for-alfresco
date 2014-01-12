package com.github.dynamicextensionsalfresco.policy.samples;

import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
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
