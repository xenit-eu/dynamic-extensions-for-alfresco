package com.github.dynamicextensionsalfresco.policy.samples;

import com.github.dynamicextensionsalfresco.behaviours.annotations.AssociationPolicy;
import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
import com.github.dynamicextensionsalfresco.behaviours.annotations.Event;
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
