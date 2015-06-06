package com.github.dynamicextensionsalfresco.policy;

import com.github.dynamicextensionsalfresco.policy.samples.*;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Verify correct registration against {@link PolicyComponent} by the {@link AnnotationBasedBehaviourRegistrar}.
 *
 * @author Laurent Van der Linden
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BehaviourRegistrarTest {
	@Autowired
	PolicyComponent policyComponent;

	@Autowired
	ContentBehaviour contentBehaviour;
	@Autowired
	AssociationBehaviourInheritClassAndContain associationBehaviourInheritClassAndContain;
	@Autowired
	AssociationBehaviourContain associationBehaviourContain;
	@Autowired
	AssociationBehaviourVoid associationBehaviourVoid;
	@Autowired
	PropertyBehaviour propertyBehaviour;

	@Test
	public void testContentBehaviour() {
		verify(policyComponent).bindClassBehaviour(
				eq(NodeServicePolicies.OnCreateNodePolicy.QNAME),
				eq(ContentModel.TYPE_CONTENT),
				refEq(new JavaBehaviour(contentBehaviour, "onCreateNode", Behaviour.NotificationFrequency.EVERY_EVENT),
						"proxies", "disabled", "methodReference")
		);
	}

	@Test
	public void testAssociationBehaviour() {
		verify(policyComponent).bindAssociationBehaviour(
				eq(NodeServicePolicies.OnCreateAssociationPolicy.QNAME),
				eq(ContentModel.TYPE_CONTENT),
				eq(ContentModel.ASSOC_CONTAINS),
				refEq(new JavaBehaviour(associationBehaviourInheritClassAndContain, "onCreateAssociation", Behaviour.NotificationFrequency.FIRST_EVENT),
						"proxies", "disabled", "methodReference")
		);
		verify(policyComponent).bindAssociationBehaviour(
				eq(NodeServicePolicies.OnCreateAssociationPolicy.QNAME),
				eq(ContentModel.TYPE_FOLDER),
				eq(ContentModel.ASSOC_CONTAINS),
				refEq(new JavaBehaviour(associationBehaviourContain, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT),
						"proxies", "disabled", "methodReference")
		);
		verify(policyComponent).bindAssociationBehaviour(
				eq(NodeServicePolicies.OnCreateAssociationPolicy.QNAME),
				eq(associationBehaviourVoid),
				refEq(new JavaBehaviour(associationBehaviourVoid, "onCreateAssociation", Behaviour.NotificationFrequency.EVERY_EVENT),
						"proxies", "disabled", "methodReference")
		);
	}

	@Test
	public void testPropertyBehaviour() {
		verify(policyComponent).bindPropertyBehaviour(
				eq(DummyPropertyPolicy.QNAME),
				eq(ContentModel.TYPE_CONTENT),
				eq(ContentModel.PROP_NAME),
				refEq(new JavaBehaviour(propertyBehaviour, "onNewValue", Behaviour.NotificationFrequency.EVERY_EVENT),
						"proxies", "disabled", "methodReference")
		);
	}
}
