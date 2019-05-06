package com.github.dynamicextensionsalfresco.policy.samples;

import com.github.dynamicextensionsalfresco.behaviours.annotations.AssociationPolicy;
import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
import com.github.dynamicextensionsalfresco.behaviours.annotations.Event;
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
