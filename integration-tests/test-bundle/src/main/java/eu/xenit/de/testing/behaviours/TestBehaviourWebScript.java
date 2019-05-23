package eu.xenit.de.testing.behaviours;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import eu.xenit.de.testing.Model;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@WebScript(families = TEST_WEBSCRIPTS_FAMILY, baseUri = TEST_WEBSCRIPTS_BASE_URI + "/behaviours")
public class TestBehaviourWebScript {

    private RetryingTransactionHelper retryingTransactionHelper;
    private NodeService nodeService;
    private FileFolderService fileFolderService;

    private NodeRef testFolder;

    @Autowired
    public TestBehaviourWebScript(NodeService nodeService,
            FileFolderService fileFolderService,
            RetryingTransactionHelper retryingTransactionHelper) {
        this.nodeService = nodeService;
        this.fileFolderService = fileFolderService;
        this.retryingTransactionHelper = retryingTransactionHelper;

        AuthenticationUtil.runAsSystem(() -> testFolder = createOrResetTestFolder());
    }


    @Uri("/OnCreateNodePolicy")
    @Transaction
    public ResponseEntity<String> testBehaviour_OnCreateNodePolicy() {

        final NodeRef createdNode = retryingTransactionHelper.doInTransaction(
                () -> nodeService.createNode(
                        testFolder,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName("{test.model}", "testBehaviour_OnCreateNodePolicy"),
                        Model.TYPE_TESTDOCUMENT),
                false,
                true).getChildRef();

        if (createdNode == null) {
            return new ResponseEntity<>("Test node was not created correctly", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final String testBehaviourProperty = (String) nodeService
                .getProperty(createdNode, Model.PROP_TESTBEHAVIOURPROPERTY);

        return new ResponseEntity<>(testBehaviourProperty, HttpStatus.OK);

    }

    private NodeRef createOrResetTestFolder() {
        final NodeRef companyHome = getCompanyHome();

        final NodeRef existingTestFolder = fileFolderService.searchSimple(companyHome, "Behaviours");

        if (existingTestFolder != null) {
            fileFolderService.delete(existingTestFolder);
        }

        return fileFolderService.create(companyHome, "Behaviours", ContentModel.TYPE_FOLDER).getNodeRef();
    }

    private NodeRef getCompanyHome() {
        StoreRef storeRef = new StoreRef("workspace", "SpacesStore");
        final NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
        QName qname = QName.createQName("http://www.alfresco.org/model/application/1.0", "company_home");
        List<ChildAssociationRef> assocRefs = this.nodeService
                .getChildAssocs(rootNodeRef, ContentModel.ASSOC_CHILDREN, qname);
        return assocRefs.get(0).getChildRef();
    }

}
