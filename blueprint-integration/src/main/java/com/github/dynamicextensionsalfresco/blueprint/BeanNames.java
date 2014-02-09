package com.github.dynamicextensionsalfresco.blueprint;

/**
 * Contains constants for bean names used by {@link DynamicExtensionsApplicationContext}.
 * 
 * @author Laurens Fridael
 * 
 */
public class BeanNames {

	static final String ANNOTATION_BASED_ACTION_REGISTRAR = "annotationBasedActionRegistrar";

	static final String ANNOTATION_BASED_BEHAVIOUR_REGISTRAR = "annotationBasedBehaviourRegistrar";

	static final String ANNOTATION_BASED_WEB_SCRIPT_BUILDER = "annotationBasedWebScriptBuilder";

	static final String ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR = "annotationBasedWebScriptRegistrar";

	static final String TYPE_BASED_WORKFLOW_REGISTRAR = "typeBasedWorkflowRegistrar";

	static final String AUTO_PROXY_CREATOR = "autoProxyCreator";

	static final String BEHAVIOUR_PROXY_FACTORY = "behaviourProxyFactory";

	static final String HANDLER_METHOD_ARGUMENTS_RESOLVER = "handlerMethodArgumentsResolver";

	static final String M2_MODEL_LIST_FACTORY = "m2ModelListFactoryBean";

	static final String MODEL_REGISTRAR = "modelRegistrar";

	static final String WORKFLOW_DEFINITION_REGISTRAR = "workflowDefinitionRegistrar";

	static final String PROXY_POLICY_COMPONENT = "proxyPolicyComponent";

	static final String SEARCH_PATH_REGISTRY_MANAGER = "searchPathRegistryManager";

	static final String STRING_VALUE_CONVERTER = "stringValueConverter";

    static final String RESOURCE_HELPER = "resourceHelper";

    static final String BOOTSTRAP_SERVICE = "bootstrapService";

    static final String OSGI_SERVICE_REGISTRAR = "osgiServiceRegistrar";

	private BeanNames() {
	}
}
