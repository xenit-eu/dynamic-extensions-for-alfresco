package nl.runnable.alfresco.osgi.spring;

import javax.inject.Inject;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.NamespaceService;

public class Example {

	@Inject
	NodeService nodeService;

	@Inject
	NodeService namedNodeService;

	@Inject
	@AlfrescoService(ServiceType.LOW_LEVEL)
	NodeService lowLevelNodeService;

	@Inject
	NamespaceService namespaceService;

	@Inject
	CategoryService categoryService;

}
