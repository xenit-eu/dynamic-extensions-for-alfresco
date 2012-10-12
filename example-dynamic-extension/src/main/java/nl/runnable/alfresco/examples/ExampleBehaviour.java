package nl.runnable.alfresco.examples;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.behaviours.annotations.AssociationPolicy;
import nl.runnable.alfresco.behaviours.annotations.Behaviour;
import nl.runnable.alfresco.behaviours.annotations.ClassPolicy;
import nl.runnable.alfresco.behaviours.annotations.Event;

import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

@ManagedBean
@Behaviour(value = { "cm:content", "cm:folder" }, event = Event.COMMIT)
public class ExampleBehaviour implements OnUpdatePropertiesPolicy, OnCreateChildAssociationPolicy {

	@ClassPolicy(event = Event.FIRST)
	public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before,
			final Map<QName, Serializable> after) {
		System.out.printf("Updating node properties for \"%s\" from %s to %s.\n", nodeRef, before, after);
	}

	@AssociationPolicy(value = "cm:folder", association = "cm:contains")
	public void onCreateChildAssociation(final ChildAssociationRef childAssocRef, final boolean isNewNode) {
		System.out.printf("Creating child association from \"%s\" to \"%s\".\n", childAssocRef.getParentRef(),
				childAssocRef.getChildRef());
	}

}
