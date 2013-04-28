package nl.runnable.alfresco.blueprint;

/**
 * Contains constants for bean names used by {@link DynamicExtensionsApplicationContext}.
 * 
 * @author Laurens Fridael
 * 
 */
class BeanNames {

	static final String ANNOTATION_BASED_ACTION_REGISTRAR = "annotationBasedActionRegistrar";

	static final String ANNOTATION_BASED_BEHAVIOUR_REGISTRAR = "annotationBasedBehaviourRegistrar";

	static final String ANNOTATION_BASED_WEB_SCRIPT_BUILDER = "annotationBasedWebScriptBuilder";

	static final String ANNOTATION_BASED_WEB_SCRIPT_HANDLER = "annotationBasedWebScriptHandler";

	static final String ANNOTATION_BASED_WEB_SCRIPT_REGISTRY = "annotationBasedWebScriptRegistry";

	static final String BEHAVIOUR_PROXY_FACTORY = "behaviourProxyFactory";

	static final String COMPOSITE_REGISTRY_MANAGER = "compositeRegistryManager";

	static final String HANDLER_METHOD_ARGUMENTS_RESOLVER = "handlerMethodArgumentsResolver";

	static final String M2_MODEL_LIST_FACTORY = "m2ModelListFactoryBean";

	static final String EXTENSION = "extension";

	static final String MODEL_REGISTRAR = "modelRegistrar";

	static final String PROXY_POLICY_COMPONENT = "proxyPolicyComponent";

	static final String SEARCH_PATH_REGISTRY_MANAGER = "searchPathRegistryManager";

	static final String STRING_VALUE_CONVERTER = "stringValueConverter";

	static final String AUTO_PROXY_CREATOR = "autoProxyCreator";

	private BeanNames() {
	}
}
