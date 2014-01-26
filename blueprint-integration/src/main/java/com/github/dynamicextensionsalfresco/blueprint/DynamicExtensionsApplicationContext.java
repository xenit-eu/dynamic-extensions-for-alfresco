package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.ContentComparator;
import com.github.dynamicextensionsalfresco.actions.AnnotationBasedActionRegistrar;
import com.github.dynamicextensionsalfresco.aop.DynamicExtensionsAdvisorAutoProxyCreator;
import com.github.dynamicextensionsalfresco.models.M2ModelListFactoryBean;
import com.github.dynamicextensionsalfresco.models.RepositoryModelRegistrar;
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistry;
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistryManager;
import com.github.dynamicextensionsalfresco.policy.AnnotationBasedBehaviourRegistrar;
import com.github.dynamicextensionsalfresco.policy.DefaultBehaviourProxyFactory;
import com.github.dynamicextensionsalfresco.policy.ProxyPolicyComponentFactoryBean;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptBuilder;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRegistrar;
import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry;
import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver;
import com.github.dynamicextensionsalfresco.webscripts.arguments.StringValueConverter;
import com.github.dynamicextensionsalfresco.workflow.WorkflowDefinitionRegistrar;
import com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistrar;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.util.StringUtils;
import org.xml.sax.EntityResolver;

import java.io.IOException;

/**
 * {@link ApplicationContext} for Dynamic Extensions.
 * <p>
 * This implementation populates the {@link BeanFactory} with {@link BeanDefinition}s by scanning for classes in
 * packages listed in the bundle's {@value #SPRING_CONFIGURATION_HEADER} header. This enables XML-free configuration.
 * <p>
 * Using annotation for configuring the ApplicationContext is the default and preferred option. (See below for notes on
 * configuring the ApplicationContext through XML.)
 * <p>
 * This class also registers infrastructure beans that facilitate annotation-based Behaviours, Actions and Web Scripts.
 * <p>
 * <h2>Creating ApplicationContexts using XML configuration</h2>
 * <p>
 * If Spring XML configuration is present in the classpath folder <code>/META-INF/spring</code> this implementation
 * falls back on creating an {@link ApplicationContext} by combining configuration from all XML files in this folder.
 * <p>
 * This implementation works around classloading issues for Spring XML {@link NamespaceHandler}s when <em>embedding</em>
 * the Blueprint container within an application that exposes Spring library classes through system packages. The main
 * difference from a more typical setup is that the Spring library classes are NOT loaded as OSGi bundles by the OSGi
 * framework, but through the embedding application's classloader.
 * <p>
 * Specifically, this class loads bean definitions by creating an {@link XmlBeanDefinitionReader} configured with a
 * {@link NamespaceHandlerResolver} and an {@link EntityResolver} obtained from the embedding application. This, in
 * turn, causes the Spring XML configuration to be handled by {@link NamespaceHandler}s from the Spring libraries
 * bundled with Alfresco. These services must have a <code>hostApplication</code> property that is set to the value
 * "alfresco" for this to work.
 * <p>
 * The alternative would be to load the Spring libraries as OSGi bundles. (The Spring JARs are already OSGi-enabled.)
 * While this could be considered a cleaner approach, it has the disadvantage of loading the Spring libraries twice: in
 * both the embedding application and the OSGi container.
 * 
 * @author Laurens Fridael
 * 
 */
class DynamicExtensionsApplicationContext extends OsgiBundleXmlApplicationContext {

	private static final String SPRING_CONFIGURATION_HEADER = "Alfresco-Spring-Configuration";

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	private static final String HOST_APPLICATION_ALFRESCO_FILTER = "(hostApplication=alfresco)";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final boolean hasXmlConfiguration;

	/* Main operations */

	DynamicExtensionsApplicationContext(final String[] configurationLocations, final ApplicationContext parent) {
		super(configurationLocations, parent);
		hasXmlConfiguration = (configurationLocations != null && configurationLocations.length > 0);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new OsgiAutowireBeanFactory(getInternalParentBeanFactory(), getBundleContext());
	}

	@Override
	protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws IOException {
        final boolean isAlfrescoDynamicExtension = isAlfrescoDynamicExtension();

		if (hasSpringConfigurationHeader()) {
			if (hasXmlConfiguration() && logger.isWarnEnabled()) {
				logger.warn(String
						.format("Spring XML configuration at /META-INF/spring will be ignored due to the presence of the '%s' header.",
								SPRING_CONFIGURATION_HEADER));
			}
            scanPackages(beanFactory, getSpringConfigurationPackages());
		} else if (hasXmlConfiguration()) {
			try {
				super.loadBeanDefinitions(beanFactory);
			} catch (final BeanDefinitionParsingException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error parsing bean definitions.", e);
				}
			}
		} else if (isAlfrescoDynamicExtension) {
            // fall back to the OSGi "Export-Package" header (often created by BND tool)
            // this provides a sensible default to reduce minimal required configuration
            scanPackages(beanFactory, getBundleExportPackages());
        }

        if (isAlfrescoDynamicExtension) {
			registerInfrastructureBeans(beanFactory);
		}
	}

	/* Utility operations */

	protected boolean hasSpringConfigurationHeader() {
		return getBundle().getHeaders().get(SPRING_CONFIGURATION_HEADER) != null;
	}

	protected boolean isAlfrescoDynamicExtension() {
		return Boolean.valueOf(getBundle().getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

    private void scanPackages(DefaultListableBeanFactory beanFactory, String[] configurationPackages) {
        if (configurationPackages != null) {
            final Descriptor serverDescriptor = getService(DescriptorService.class).getServerDescriptor();
            final ClassPathBeanDefinitionScanner scanner = new AlfrescoPlatformBeanDefinitionScanner(beanFactory,
                    serverDescriptor);
            scanner.setResourceLoader(this);
            scanner.scan(configurationPackages);
        }
    }

    @Override
	protected void initBeanDefinitionReader(final XmlBeanDefinitionReader beanDefinitionReader) {
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setNamespaceHandlerResolver(new CompositeNamespaceHandlerResolver(
				getOsgiNamespaceHandlerResolver(), new DefaultNamespaceHandlerResolver(getClassLoader()),
				getHostNamespaceHandlerResolver()));
		beanDefinitionReader.setEntityResolver(new CompositeEntityResolver(getOsgiEntityResolver(),
				new DelegatingEntityResolver(getClassLoader()), getHostEntityResolver()));
	}

	/**
	 * Registers infrastructure beans for additional services such as annotation-based Behaviours.
	 * 
	 * @param beanFactory
	 */
	protected void registerInfrastructureBeans(final DefaultListableBeanFactory beanFactory) {
        registerContentSupportBeans(beanFactory);
        registerModelDeploymentBeans(beanFactory);
        registerWorkflowDeployment(beanFactory);
        registerAnnotationBasedBehaviourBeans(beanFactory);
        registerAnnotationBasedActionBeans(beanFactory);
        registerAnnotationBasedWebScriptBeans(beanFactory);
        registerAopProxyBeans(beanFactory);
        registerWorkflowBeans(beanFactory);
        registerOsgiServiceBeans(beanFactory);
    }

    /**
	 * Registers the infrastructure beans necessary for automatic XML content model deployment.
	 * 
	 * @param beanFactory
	 */
	protected void registerModelDeploymentBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(BeanNames.M2_MODEL_LIST_FACTORY) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.M2_MODEL_LIST_FACTORY,
					BeanDefinitionBuilder.rootBeanDefinition(M2ModelListFactoryBean.class)
                    .getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.MODEL_REGISTRAR) == false) {
			beanFactory.registerBeanDefinition(
                BeanNames.MODEL_REGISTRAR,
                BeanDefinitionBuilder.rootBeanDefinition(RepositoryModelRegistrar.class)
                    .addPropertyReference("models", BeanNames.M2_MODEL_LIST_FACTORY)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .setInitMethodName("registerModels").setDestroyMethodName("unregisterModels")
                    .getBeanDefinition());
		}
	}

    private void registerWorkflowDeployment(DefaultListableBeanFactory beanFactory) {
        if (beanFactory.containsBeanDefinition(BeanNames.WORKFLOW_DEFINITION_REGISTRAR) == false) {
            beanFactory.registerBeanDefinition(
                BeanNames.WORKFLOW_DEFINITION_REGISTRAR,
                BeanDefinitionBuilder.rootBeanDefinition(WorkflowDefinitionRegistrar.class)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .getBeanDefinition());
        }
    }

	/**
	 * Registers the infrastructure beans that facilitate annotation-based Behaviours.
	 * 
	 * @param beanFactory
	 */
	protected void registerAnnotationBasedBehaviourBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(BeanNames.BEHAVIOUR_PROXY_FACTORY) == false) {
			beanFactory.registerBeanDefinition(BeanNames.BEHAVIOUR_PROXY_FACTORY, BeanDefinitionBuilder
					.rootBeanDefinition(DefaultBehaviourProxyFactory.class).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.PROXY_POLICY_COMPONENT) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.PROXY_POLICY_COMPONENT,
					BeanDefinitionBuilder.rootBeanDefinition(ProxyPolicyComponentFactoryBean.class)
							.addPropertyValue("policyComponent", getService(PolicyComponent.class))
							.addPropertyReference("behaviourProxyFactory", BeanNames.BEHAVIOUR_PROXY_FACTORY)
							.getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_BEHAVIOUR_REGISTRAR) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_BEHAVIOUR_REGISTRAR,
					BeanDefinitionBuilder.rootBeanDefinition(AnnotationBasedBehaviourRegistrar.class)
							.addPropertyReference("policyComponent", BeanNames.PROXY_POLICY_COMPONENT)
							.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
							.setInitMethodName("bindBehaviours").getBeanDefinition());
		}
	}

	/**
	 * Registers the infrastructure beans necessary for annotation-based Actions.
	 * 
	 * @param beanFactory
	 */
	protected void registerAnnotationBasedActionBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_ACTION_REGISTRAR) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_ACTION_REGISTRAR,
					BeanDefinitionBuilder.rootBeanDefinition(AnnotationBasedActionRegistrar.class)
							.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
							.setInitMethodName("registerAnnotationBasedActions")
							.setDestroyMethodName("unregisterAnnotationBasedActions").getBeanDefinition());
		}
	}

	/**
	 * Registers the infrastructure beans for annotation-based Web Scripts.
	 * 
	 * @param beanFactory
	 */
	protected void registerAnnotationBasedWebScriptBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(BeanNames.STRING_VALUE_CONVERTER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.STRING_VALUE_CONVERTER,
					BeanDefinitionBuilder.rootBeanDefinition(StringValueConverter.class)
							.addPropertyValue("namespacePrefixResolver", getService(NamespacePrefixResolver.class))
							.getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER) == false) {
			beanFactory.registerBeanDefinition(BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER,
					BeanDefinitionBuilder.rootBeanDefinition(HandlerMethodArgumentsResolver.class)
							.addPropertyReference("stringValueConverter", BeanNames.STRING_VALUE_CONVERTER)
							.setInitMethodName("initializeArgumentResolvers").getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationWebScriptBuilder.class)
							.addPropertyReference("handlerMethodArgumentsResolver",
									BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationWebScriptRegistrar.class)
							.addPropertyReference("annotationBasedWebScriptBuilder",
									BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER)
							.addPropertyValue("webScriptUriRegistry", getService(WebScriptUriRegistry.class))
							.setInitMethodName("registerWebScripts").setDestroyMethodName("unregisterWebScripts")
							.getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.SEARCH_PATH_REGISTRY_MANAGER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.SEARCH_PATH_REGISTRY_MANAGER,
					BeanDefinitionBuilder.rootBeanDefinition(SearchPathRegistryManager.class)
							.addPropertyValue("searchPathRegistry", getService(SearchPathRegistry.class))
							.addPropertyValue("stores", new BundleStore(getBundle()))
							.addPropertyValue("templateProcessor", getService(TemplateProcessor.class))
							.setInitMethodName("registerStores").setDestroyMethodName("unregisterStores")
							.getBeanDefinition());
		}
	}

	protected void registerAopProxyBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(BeanNames.AUTO_PROXY_CREATOR) == false) {
			beanFactory.registerBeanDefinition(BeanNames.AUTO_PROXY_CREATOR,
					BeanDefinitionBuilder.rootBeanDefinition(DynamicExtensionsAdvisorAutoProxyCreator.class)
							.getBeanDefinition());
		}
	}

    protected void registerWorkflowBeans(final DefaultListableBeanFactory beanFactory) {
        if (!beanFactory.containsBeanDefinition(BeanNames.TYPE_BASED_WORKFLOW_REGISTRAR)) {
            beanFactory.registerBeanDefinition(BeanNames.TYPE_BASED_WORKFLOW_REGISTRAR,
                BeanDefinitionBuilder.rootBeanDefinition(WorkflowTaskRegistrar.class).getBeanDefinition()
            );
        }
    }

    private void registerContentSupportBeans(DefaultListableBeanFactory beanFactory) {
        if (beanFactory.containsBeanDefinition(BeanNames.CONTENT_COMPARATOR) == false) {
            beanFactory.registerBeanDefinition(
                BeanNames.CONTENT_COMPARATOR,
                BeanDefinitionBuilder.rootBeanDefinition(ContentComparator.class)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .getBeanDefinition());
        }
    }

    protected void registerOsgiServiceBeans(final DefaultListableBeanFactory beanFactory) {
        if (beanFactory.containsBeanDefinition(BeanNames.OSGI_SERVICE_REGISTRAR) == false) {
            beanFactory.registerBeanDefinition(BeanNames.OSGI_SERVICE_REGISTRAR, BeanDefinitionBuilder
                .rootBeanDefinition(OsgiServiceRegistrar.class).getBeanDefinition());
        }
    }

    /**
     * Use the deprecated PackageAdmin to get the list of exported packages.
     */
    @SuppressWarnings("deprecation")
    protected String[] getBundleExportPackages() {
        final String exportPackageHeader = getBundle().getHeaders().get(Constants.EXPORT_PACKAGE);
        if (StringUtils.hasText(exportPackageHeader)) {
            final ServiceReference<PackageAdmin> serviceReference = getBundleContext().getServiceReference(PackageAdmin.class);
            try {
                final PackageAdmin packageAdmin = getBundleContext().getService(serviceReference);
                final ExportedPackage[] exportedPackages = packageAdmin.getExportedPackages(getBundle());
                String[] packageNames = new String[exportedPackages.length];
                for (int i = 0; i < exportedPackages.length; i++) {
                    packageNames[i] = exportedPackages[i].getName();
                }
                return packageNames;
            } finally {
                getBundleContext().ungetService(serviceReference);
            }
        }
        return null;
    }

	protected String[] getSpringConfigurationPackages() {
		final String header = getBundle().getHeaders().get(SPRING_CONFIGURATION_HEADER);
		if (StringUtils.hasText(header)) {
			return header.split(",");
		} else {
			return null;
		}
	}

	/* Dependencies */

	protected <T> T getService(final Class<T> clazz) {
		final ServiceReference<T> serviceReference = getBundleContext().getServiceReference(clazz);
		if (serviceReference == null) {
			throw new IllegalStateException(String.format("Cannot obtain '%s' service reference from bundle context.",
					clazz.getName()));
		}
		return getBundleContext().getService(serviceReference);
	}

	protected NamespaceHandlerResolver getOsgiNamespaceHandlerResolver() {
		try {
			final BundleContext bundleContext = getBundleContext();
			return (NamespaceHandlerResolver) bundleContext.getService(bundleContext.getServiceReferences(
					NamespaceHandlerResolver.class.getName(), BundleUtils.createNamespaceFilter(bundleContext))[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtains the {@link NamespaceHandlerResolver} using a {@link ServiceReference}.
	 */
	protected NamespaceHandlerResolver getHostNamespaceHandlerResolver() {
		try {
			final BundleContext bundleContext = getBundleContext();
			return (NamespaceHandlerResolver) bundleContext.getService(bundleContext.getServiceReferences(
					NamespaceHandlerResolver.class.getName(), HOST_APPLICATION_ALFRESCO_FILTER)[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected EntityResolver getOsgiEntityResolver() {
		try {
			final BundleContext bundleContext = getBundleContext();
			return (EntityResolver) bundleContext.getService(bundleContext.getServiceReferences(
					EntityResolver.class.getName(), BundleUtils.createNamespaceFilter(bundleContext))[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtains the {@link EntityResolver} using a {@link ServiceReference}.
	 */
	protected EntityResolver getHostEntityResolver() {
		try {
			final BundleContext bundleContext = getBundleContext();
			return (EntityResolver) bundleContext.getService(bundleContext.getServiceReferences(
					EntityResolver.class.getName(), HOST_APPLICATION_ALFRESCO_FILTER)[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/* Configuration */

    protected boolean hasXmlConfiguration() {
		return hasXmlConfiguration;
	}

}