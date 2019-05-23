package eu.xenit.de.testing.behaviours;

import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
import com.github.dynamicextensionsalfresco.behaviours.annotations.ClassPolicy;
import eu.xenit.de.testing.Model;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Behaviour("test:document")
public class TestBehaviour implements OnCreateNodePolicy {

    private static final Logger logger = LoggerFactory.getLogger(TestBehaviour.class);

    @Autowired
    private NodeService nodeService;

    @Override
    @ClassPolicy
    public void onCreateNode(ChildAssociationRef childAssocRef) {

        nodeService.setProperty(
                childAssocRef.getChildRef(),
                Model.PROP_TESTBEHAVIOURPROPERTY,
                "TestBehaviour successfully processed");

    }
}
