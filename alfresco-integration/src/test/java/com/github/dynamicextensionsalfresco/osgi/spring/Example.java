package nl.runnable.alfresco.osgi.spring;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.NamespaceService;
import org.springframework.beans.factory.annotation.Autowired;

public class Example {

	@Autowired
	NodeService nodeService;

	@Autowired
	NodeService namedNodeService;

	@Autowired
	@AlfrescoService(ServiceType.LOW_LEVEL)
	NodeService lowLevelNodeService;

	@Autowired
	NamespaceService namespaceService;

	@Autowired
	CategoryService categoryService;

}
