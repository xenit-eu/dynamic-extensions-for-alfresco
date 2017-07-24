package com.github.dynamicextensionsalfresco.blueprint

import com.github.dynamicextensionsalfresco.BeanNames
import com.github.dynamicextensionsalfresco.actions.AnnotationBasedActionRegistrar
import com.github.dynamicextensionsalfresco.aop.DynamicExtensionsAdvisorAutoProxyCreator
import com.github.dynamicextensionsalfresco.event.EventBus
import com.github.dynamicextensionsalfresco.event.events.SpringContextException
import com.github.dynamicextensionsalfresco.messages.MessagesRegistrar
import com.github.dynamicextensionsalfresco.metrics.SpringTimer
import com.github.dynamicextensionsalfresco.models.M2ModelListProvider
import com.github.dynamicextensionsalfresco.models.M2ModelResourceListProvider
import com.github.dynamicextensionsalfresco.models.RepositoryModelRegistrar
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistry
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistryManager
import com.github.dynamicextensionsalfresco.policy.AnnotationBasedBehaviourRegistrar
import com.github.dynamicextensionsalfresco.policy.DefaultBehaviourProxyFactory
import com.github.dynamicextensionsalfresco.policy.ProxyPolicyComponentFactoryBean
import com.github.dynamicextensionsalfresco.quartz.QuartzJobRegistrar
import com.github.dynamicextensionsalfresco.resources.DefaultBootstrapService
import com.github.dynamicextensionsalfresco.resources.ResourceHelper
import com.github.dynamicextensionsalfresco.web.WebResourcesRegistrar
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptBuilder
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRegistrar
import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry
import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver
import com.github.dynamicextensionsalfresco.webscripts.arguments.StringValueConverter
import com.github.dynamicextensionsalfresco.workflow.WorkflowDefinitionRegistrar
import com.github.dynamicextensionsalfresco.workflow.activiti.DefaultWorkflowTaskRegistry
import com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistrar
import com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistry
import org.alfresco.service.descriptor.DescriptorService
import org.alfresco.service.namespace.NamespacePrefixResolver
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext
import org.osgi.framework.Constants
import org.osgi.service.packageadmin.PackageAdmin
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver
import org.springframework.beans.factory.xml.DelegatingEntityResolver
import org.springframework.beans.factory.xml.NamespaceHandlerResolver
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.ApplicationContext
import org.springframework.extensions.webscripts.TemplateProcessor
import org.springframework.util.StringUtils
import org.xml.sax.EntityResolver
import java.io.IOException

/**
 * [ApplicationContext] for Dynamic Extensions.
 *
 *
 * This implementation populates the [BeanFactory] with [BeanDefinition]s by scanning for classes in
 * packages listed in the bundle's {@value #SPRING_CONFIGURATION_HEADER} header. This enables XML-free configuration.
 *
 *
 * Using annotation for configuring the ApplicationContext is the default and preferred option. (See below for notes on
 * configuring the ApplicationContext through XML.)
 *
 *
 * This class also registers infrastructure beans that facilitate annotation-based Behaviours, Actions and Web Scripts.
 *
 *
 * Creating ApplicationContexts using XML configuration
 *
 *
 * If Spring XML configuration is present in the classpath folder `/META-INF/spring` this implementation
 * falls back on creating an [ApplicationContext] by combining configuration from all XML files in this folder.
 *
 *
 * This implementation works around classloading issues for Spring XML [NamespaceHandler]s when *embedding*
 * the Blueprint container within an application that exposes Spring library classes through system packages. The main
 * difference from a more typical setup is that the Spring library classes are NOT loaded as OSGi bundles by the OSGi
 * framework, but through the embedding application's classloader.
 *
 *
 * Specifically, this class loads bean definitions by creating an [XmlBeanDefinitionReader] configured with a
 * [NamespaceHandlerResolver] and an [EntityResolver] obtained from the embedding application. This, in
 * turn, causes the Spring XML configuration to be handled by [NamespaceHandler]s from the Spring libraries
 * bundled with Alfresco. These services must have a `hostApplication` property that is set to the value
 * "alfresco" for this to work.
 *
 *
 * The alternative would be to load the Spring libraries as OSGi bundles. (The Spring JARs are already OSGi-enabled.)
 * While this could be considered a cleaner approach, it has the disadvantage of loading the Spring libraries twice: in
 * both the embedding application and the OSGi container.

 * @author Laurens Fridael
 */
class DynamicExtensionsApplicationContext(configurationLocations: Array<String>?, parent: ApplicationContext) :
		OsgiBundleXmlApplicationContext(configurationLocations, parent) {

    private val log = LoggerFactory.getLogger(javaClass)

    protected val hasXmlConfiguration: Boolean = configurationLocations?.isNotEmpty() ?: false

    override fun createBeanFactory(): DefaultListableBeanFactory {
        return OsgiAutowireBeanFactory(internalParentBeanFactory, bundleContext)
    }

    @Throws(IOException::class)
    override fun loadBeanDefinitions(beanFactory: DefaultListableBeanFactory) {
        val isAlfrescoDynamicExtension = isAlfrescoDynamicExtension()

        if (hasSpringConfigurationHeader()) {
            if (hasXmlConfiguration() && log.isWarnEnabled) {
                log.warn("Spring XML configuration at /META-INF/spring will be ignored due to the presence of the '$SPRING_CONFIGURATION_HEADER' header.")
            }
            scanPackages(beanFactory, getSpringConfigurationPackages())
        } else if (hasXmlConfiguration()) {
            try {
                super.loadBeanDefinitions(beanFactory)
            } catch (e: BeanDefinitionParsingException) {
				log.warn("Error parsing bean definitions.", e)
			}

        } else if (isAlfrescoDynamicExtension) {
            // fall back to the OSGi "Export-Package" header (often created by BND tool)
            // this provides a sensible default to reduce minimal required configuration
            scanPackages(beanFactory, getBundleExportPackages())
        }

        if (isAlfrescoDynamicExtension) {
            registerInfrastructureBeans(beanFactory)
        }
    }

    /* Utility operations */

    protected fun hasSpringConfigurationHeader(): Boolean {
        return bundle.headers.get(SPRING_CONFIGURATION_HEADER) != null
    }

    protected fun isAlfrescoDynamicExtension(): Boolean {
        return java.lang.Boolean.valueOf(bundle.headers.get(ALFRESCO_DYNAMIC_EXTENSION_HEADER))
    }

    private fun scanPackages(beanFactory: DefaultListableBeanFactory, configurationPackages: List<String>?) {
        if (configurationPackages != null) {
            val serverDescriptor = getService(DescriptorService::class.java).serverDescriptor
            val scanner = AlfrescoPlatformBeanDefinitionScanner(beanFactory, serverDescriptor)
            scanner.resourceLoader = this
            scanner.scan(*configurationPackages.toTypedArray())
        }
    }

    override fun initBeanDefinitionReader(beanDefinitionReader: XmlBeanDefinitionReader?) {
        beanDefinitionReader!!.resourceLoader = this
        beanDefinitionReader.setNamespaceHandlerResolver(
                CompositeNamespaceHandlerResolver(
                        getOsgiNamespaceHandlerResolver(),
                        DefaultNamespaceHandlerResolver(classLoader
                ), getHostNamespaceHandlerResolver())
        )
        beanDefinitionReader.setEntityResolver(CompositeEntityResolver(getOsgiEntityResolver(), DelegatingEntityResolver(classLoader), getHostEntityResolver()))
    }

    /**
     * Registers infrastructure beans for additional services such as annotation-based Behaviours.

     * @param beanFactory
     */
    protected fun registerInfrastructureBeans(beanFactory: DefaultListableBeanFactory) {
        registerContentSupportBeans(beanFactory)
        registerModelDeploymentBeans(beanFactory)
        registerWorkflowDeployment(beanFactory)
        registerMessagesDeployment(beanFactory)
        registerAnnotationBasedBehaviourBeans(beanFactory)
        registerAnnotationBasedActionBeans(beanFactory)
        registerAnnotationBasedWebScriptBeans(beanFactory)
        registerAopProxyBeans(beanFactory)
        registerWorkflowBeans(beanFactory)
        registerOsgiServiceBeans(beanFactory)
        registerQuartzBeans(beanFactory)
        registerMetrics(beanFactory)
        registerWebResources(beanFactory)
    }

    /**
     * Registers the infrastructure beans necessary for automatic XML content model deployment.

     * @param beanFactory
     */
    protected fun registerModelDeploymentBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.M2_MODEL_LIST_FACTORY, M2ModelResourceListProvider::class.java)

        beanFactory.bean(BeanNames.MODEL_REGISTRAR, RepositoryModelRegistrar::class.java) {
            autowireByType()
            setInitMethodName("registerModels")
            setDestroyMethodName("unregisterModels")
        }
    }

    private fun registerWorkflowDeployment(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.WORKFLOW_DEFINITION_REGISTRAR, WorkflowDefinitionRegistrar::class.java) {
            autowireByType()
        }
    }

    private fun registerMessagesDeployment(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.MESSAGES_REGISTRAR, MessagesRegistrar::class.java) {
            autowireByType()
        }
    }


    /**
     * Registers the infrastructure beans that facilitate annotation-based Behaviours.

     * @param beanFactory
     */
    protected fun registerAnnotationBasedBehaviourBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.BEHAVIOUR_PROXY_FACTORY, DefaultBehaviourProxyFactory::class.java) {
            autowireByType()
        }
        beanFactory.bean(BeanNames.PROXY_POLICY_COMPONENT, ProxyPolicyComponentFactoryBean::class.java) {
            addPropertyReference("policyComponent", "policyComponent")
            addPropertyReference("behaviourProxyFactory", BeanNames.BEHAVIOUR_PROXY_FACTORY.id())

        }
        beanFactory.bean(BeanNames.ANNOTATION_BASED_BEHAVIOUR_REGISTRAR, AnnotationBasedBehaviourRegistrar::class.java) {
            addPropertyReference("policyComponent", BeanNames.PROXY_POLICY_COMPONENT.id())
            autowireByType()
            setInitMethodName("bindBehaviours")
        }
    }

    /**
     * Registers the infrastructure beans necessary for annotation-based Actions.

     * @param beanFactory
     */
    protected fun registerAnnotationBasedActionBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.ANNOTATION_BASED_ACTION_REGISTRAR, AnnotationBasedActionRegistrar::class.java) {
            autowireByType()
            setInitMethodName("registerAnnotationBasedActions")
            setDestroyMethodName("unregisterAnnotationBasedActions")
        }
    }

    /**
     * Registers the infrastructure beans for annotation-based Web Scripts.

     * @param beanFactory
     */
    protected fun registerAnnotationBasedWebScriptBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.STRING_VALUE_CONVERTER, StringValueConverter::class.java) {
            addPropertyValue("namespacePrefixResolver", getService(NamespacePrefixResolver::class.java))
        }
        beanFactory.bean(BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER, HandlerMethodArgumentsResolver::class.java) {
            addPropertyReference("stringValueConverter", BeanNames.STRING_VALUE_CONVERTER.id())
            addPropertyValue("bundleContext", bundleContext)
            setInitMethodName("initializeArgumentResolvers")
        }
        beanFactory.bean(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER, AnnotationWebScriptBuilder::class.java) {
            addPropertyReference("handlerMethodArgumentsResolver",BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER.id());
        }
        beanFactory.bean(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR, AnnotationWebScriptRegistrar::class.java) {
            addPropertyReference("annotationBasedWebScriptBuilder", BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER.id())
            addPropertyValue("webScriptUriRegistry", getService(WebScriptUriRegistry::class.java))
            setInitMethodName("registerWebScripts")
            setDestroyMethodName("unregisterWebScripts")
        }
        beanFactory.bean(BeanNames.SEARCH_PATH_REGISTRY_MANAGER, SearchPathRegistryManager::class.java) {
            addPropertyValue("searchPathRegistry", getService(SearchPathRegistry::class.java))
            addPropertyValue("stores", BundleStore(bundle))
            addPropertyValue("templateProcessor", getService(TemplateProcessor::class.java))
            setInitMethodName("registerStores")
            setDestroyMethodName("unregisterStores")
        }
    }

    protected fun registerAopProxyBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.AUTO_PROXY_CREATOR, DynamicExtensionsAdvisorAutoProxyCreator::class.java)
    }

    protected fun registerWorkflowBeans(beanFactory: DefaultListableBeanFactory) {
        try {
            WorkflowTaskRegistry::class.java.classLoader.loadClass("org.activiti.engine.delegate.JavaDelegate")
        } catch (ignore: Throwable) {
            return
        }

        beanFactory.bean(BeanNames.TYPE_BASED_WORKFLOW_REGISTRAR, WorkflowTaskRegistrar::class.java) {
            addConstructorArgReference("activitiBeanRegistry")
            addConstructorArgReference(DefaultWorkflowTaskRegistry.BEAN_NAME)
        }
    }

    private fun registerContentSupportBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.RESOURCE_HELPER, ResourceHelper::class.java) {
            autowireByType()
        }
        beanFactory.bean(BeanNames.BOOTSTRAP_SERVICE, DefaultBootstrapService::class.java) {
            autowireByType()
        }
    }

    protected fun registerOsgiServiceBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.OSGI_SERVICE_REGISTRAR, OsgiServiceRegistrar::class.java)
    }

    protected fun registerQuartzBeans(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.QUARTZ_JOB_REGISTRAR, QuartzJobRegistrar::class.java) {
            autowireByType()
        }
    }

    protected fun registerMetrics(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.METRICS_TIMER, SpringTimer::class.java) {
            autowireByType()
        }
    }

    protected fun registerWebResources(beanFactory: DefaultListableBeanFactory) {
        beanFactory.bean(BeanNames.RESOURCES_WEB, WebResourcesRegistrar::class.java) {
            autowireByType()
        }
    }

    /**
     * Use the deprecated PackageAdmin to get the list of exported packages.
     */
    protected fun getBundleExportPackages(): List<String>? {
        val exportPackageHeader = bundle.headers.get(Constants.EXPORT_PACKAGE)
        if (StringUtils.hasText(exportPackageHeader)) {
            val packageAdmin = getService(PackageAdmin::class.java)
            return packageAdmin.getExportedPackages(bundle).map { it.name }
        }
        return null
    }

    protected fun getSpringConfigurationPackages(): List<String>? {
        val header = bundle.headers.get(SPRING_CONFIGURATION_HEADER)
        if (StringUtils.hasText(header)) {
            return header.split(",")
        } else {
            return null
        }
    }

    /* Dependencies */

    protected fun <T> getService(clazz: Class<T>, filter: String? = null): T {
        val serviceReference = bundleContext.getServiceReferences<T>(clazz, filter).firstOrNull()
				?: throw IllegalStateException("Cannot obtain '${clazz.name}' service reference from bundle context.")
        return bundleContext.getService<T>(serviceReference)
    }

    protected fun getOsgiNamespaceHandlerResolver(): NamespaceHandlerResolver {
		return getService(NamespaceHandlerResolver::class.java, BundleUtils.createNamespaceFilter(bundleContext))
    }

    /**
     * Obtains the [NamespaceHandlerResolver] using a [ServiceReference].
     */
    protected fun getHostNamespaceHandlerResolver(): NamespaceHandlerResolver {
		return getService(org.springframework.beans.factory.xml.NamespaceHandlerResolver::class.java, HOST_APPLICATION_ALFRESCO_FILTER)
	}

    protected fun getOsgiEntityResolver(): EntityResolver {
		return getService(EntityResolver::class.java, BundleUtils.createNamespaceFilter(bundleContext))
    }

    /**
     * Obtains the [EntityResolver] using a [ServiceReference].
     */
    protected fun getHostEntityResolver(): EntityResolver {
		return getService(EntityResolver::class.java, HOST_APPLICATION_ALFRESCO_FILTER)
    }

    /* Configuration */

    protected fun hasXmlConfiguration(): Boolean {
        return hasXmlConfiguration
    }

    override fun cancelRefresh(ex: BeansException) {
        super.cancelRefresh(ex)

        try {
            getService(EventBus::class.java).publish(SpringContextException(bundle, ex))
        } catch (bx: Exception) {
            log.error("failed to broadcast Spring refresh failure", bx)
        }

    }

    companion object {

        private val SPRING_CONFIGURATION_HEADER = "Alfresco-Spring-Configuration"

        private val ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension"

        private val HOST_APPLICATION_ALFRESCO_FILTER = "(hostApplication=alfresco)"
    }


    fun DefaultListableBeanFactory.bean(name: BeanNames, beanClass: Class<*>, body: (BeanDefinitionBuilder.() -> Unit)? = null) {
        if (!containsBeanDefinition(name.id())) {
            val beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass)
            if (body != null) {
                beanDefinitionBuilder.body()
            }
            registerBeanDefinition(name.id(), beanDefinitionBuilder.beanDefinition)
        }
    }

    fun BeanDefinitionBuilder.autowireByType() {
        setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
    }
}