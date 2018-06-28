package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.NamespaceService;
import org.springframework.beans.factory.annotation.Autowired;

public class Example {
	NodeService lowLevelNodeService;

	@Autowired
	public Example(@AlfrescoService(ServiceType.LOW_LEVEL) NodeService lowLevelNodeService) {
		this.lowLevelNodeService = lowLevelNodeService;
	}

	@Autowired
	NodeService namedNodeService;

	@Autowired
	NodeService nodeService;

	@Autowired
	NamespaceService namespaceService;

	@Autowired
	CategoryService categoryService;

}
