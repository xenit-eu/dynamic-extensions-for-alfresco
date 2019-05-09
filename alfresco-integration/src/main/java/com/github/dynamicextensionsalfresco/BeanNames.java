package com.github.dynamicextensionsalfresco;

import org.jetbrains.annotations.NotNull;

/**
 * Contains constants for bean names used by the bundle Spring application contexts.
 * 
 * @author Laurens Fridael
 * 
 */
public enum BeanNames {

	ANNOTATION_BASED_ACTION_REGISTRAR("annotationBasedActionRegistrar"),

	ANNOTATION_BASED_BEHAVIOUR_REGISTRAR("annotationBasedBehaviourRegistrar"),

	ANNOTATION_BASED_WEB_SCRIPT_BUILDER("annotationBasedWebScriptBuilder"),

	ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR("annotationBasedWebScriptRegistrar"),

	TYPE_BASED_WORKFLOW_REGISTRAR("typeBasedWorkflowRegistrar"),

	AUTO_PROXY_CREATOR("autoProxyCreator"),

	BEHAVIOUR_PROXY_FACTORY("behaviourProxyFactory"),

	HANDLER_METHOD_ARGUMENTS_RESOLVER("handlerMethodArgumentsResolver"),

	MESSAGE_CONVERTER_REGISTER("messageConverterRegister"),

	M2_MODEL_LIST_FACTORY("m2ModelListFactoryBean"),

	MODEL_REGISTRAR("modelRegistrar"),

	WORKFLOW_DEFINITION_REGISTRAR("workflowDefinitionRegistrar"),

	MESSAGES_REGISTRAR("messagesRegistrar"),

	PROXY_POLICY_COMPONENT("proxyPolicyComponent"),

	SEARCH_PATH_REGISTRY_MANAGER("searchPathRegistryManager"),

	STRING_VALUE_CONVERTER("stringValueConverter"),

    RESOURCE_HELPER("resourceHelper"),

    BOOTSTRAP_SERVICE("bootstrapService"),

    OSGI_SERVICE_REGISTRAR("osgiServiceRegistrar"),

	SCHEDULED_TASK_REGISTRAR("scheduledTaskRegistrar"),
	QUARTZ_TASK_SCHEDULER("quartzTaskScheduler"),

	RESOURCES_WEB("resourcesWeb"),

	METRICS_TIMER("metricsTimer");

	private String id;

	BeanNames(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}
}
