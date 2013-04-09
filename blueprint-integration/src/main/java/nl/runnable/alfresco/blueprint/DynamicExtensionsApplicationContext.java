/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.blueprint;

import java.io.IOException;

import nl.runnable.alfresco.actions.AnnotationBasedActionRegistrar;
import nl.runnable.alfresco.models.M2ModelListFactoryBean;
import nl.runnable.alfresco.models.ModelRegistrar;
import nl.runnable.alfresco.policy.AnnotationBasedBehaviourRegistrar;
import nl.runnable.alfresco.policy.DefaultBehaviourProxyFactory;
import nl.runnable.alfresco.policy.ProxyPolicyComponentFactoryBean;
import nl.runnable.alfresco.webscripts.AnnotationBasedWebScriptBuilder;
import nl.runnable.alfresco.webscripts.AnnotationBasedWebScriptHandler;
import nl.runnable.alfresco.webscripts.AnnotationBasedWebScriptRegistry;
import nl.runnable.alfresco.webscripts.DefaultHandlerMethodArgumentsResolver;
import nl.runnable.alfresco.webscripts.StringValueConverter;
import nl.runnable.alfresco.webscripts.integration.CompositeRegistry;
import nl.runnable.alfresco.webscripts.integration.CompositeRegistryManager;
import nl.runnable.alfresco.webscripts.integration.SearchPathRegistry;
import nl.runnable.alfresco.webscripts.integration.SearchPathRegistryManager;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.util.StringUtils;
import org.xml.sax.EntityResolver;

/**
 * {@link ApplicationContext} for Dynamic Extensions.
 * <p>
 * This implementation populates the {@link BeanFactory} with {@link BeanDefinition}s by scanning for classes in
 * packages listed in an OSGi bundle's {@link Constants#EXPORT_PACKAGE}. This enables annotation-only, XML-free
 * {@link ApplicationContext} configuration.
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

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String HOST_APPLICATION_ALFRESCO_FILTER = "(hostApplication=alfresco)";

	/* Configuration */

	private String modelLocationPattern;

	private final boolean hasXmlConfiguration;

	/* Operations */

	DynamicExtensionsApplicationContext(final String[] configurationLocations, final ApplicationContext parent) {
		super(configurationLocations, parent);
		hasXmlConfiguration = (configurationLocations != null && configurationLocations.length > 0);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new OsgiAutowireServiceBeanFactory(getInternalParentBeanFactory(), getBundleContext());
	}

	@Override
	protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws IOException {
		if (hasXmlConfiguration()) {
			loadBeanDefinitionsFromXmlConfiguration(beanFactory);
		} else {
			scanBeanDefinitionsFromExportedPackages(beanFactory);
		}
	}

	/**
	 * Populates the {@link BeanFactory} by loading the {@link BeanDefinition}s from Spring XML configuration.
	 * 
	 * @param beanFactory
	 * @throws IOException
	 */
	protected void loadBeanDefinitionsFromXmlConfiguration(final DefaultListableBeanFactory beanFactory)
			throws IOException {
		final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setNamespaceHandlerResolver(getNamespaceHandlerResolver());
		beanDefinitionReader.setEntityResolver(getEntityResolver());
		loadBeanDefinitions(beanDefinitionReader);
		registerInfrastructureBeans(beanFactory);
	}

	/**
	 * Populates the {@link BeanFactory} by scanning for classes listed in packages obtained from the Bundle's
	 * {@link Constants#EXPORT_PACKAGE} header.
	 * <p>
	 * This allows for bundles that rely on annotation-based configuration only.
	 * 
	 * @param beanFactory
	 */
	protected void scanBeanDefinitionsFromExportedPackages(final DefaultListableBeanFactory beanFactory) {
		final String[] bundleExportPackages = getBundleExportPackages();
		if (bundleExportPackages != null) {
			final Descriptor serverDescriptor = getService(DescriptorService.class).getServerDescriptor();
			final ClassPathBeanDefinitionScanner scanner = new AlfrescoPlatformBeanDefinitionScanner(beanFactory,
					serverDescriptor);
			scanner.setResourceLoader(this);
			scanner.scan(bundleExportPackages);
			registerInfrastructureBeans(beanFactory);
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not find Spring XML configuration in bundle "
						+ "and could not detect any packages in the Export-Package bundle header. "
						+ "No components will be instantiated.");
			}
		}
	}

	/* Utility operations */

	/**
	 * Registers infrastructure beans for additional services such as annotation-based Behaviours.
	 * 
	 * @param beanFactory
	 */
	protected void registerInfrastructureBeans(final DefaultListableBeanFactory beanFactory) {
		if (StringUtils.hasText(getModelLocationPattern())) {
			registerModelDeploymentBeans(beanFactory);
		}
		registerAnnotationBasedBehaviourBeans(beanFactory);
		registerAnnotationBasedActionBeans(beanFactory);
		registerAnnotationBasedWebScriptBeans(beanFactory);
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
							.addPropertyValue("locationPattern", getModelLocationPattern()).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.MODEL_REGISTRAR) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.MODEL_REGISTRAR,
					BeanDefinitionBuilder.rootBeanDefinition(ModelRegistrar.class)
							.addPropertyReference("models", BeanNames.M2_MODEL_LIST_FACTORY)
							.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
							.setInitMethodName("registerModels").setDestroyMethodName("unregisterModels")
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
	 * <p>
	 * TODO: Find out if we can load the bean definitions from Spring XML configuration. This code is too hard to
	 * maintain.
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
					BeanDefinitionBuilder.rootBeanDefinition(DefaultHandlerMethodArgumentsResolver.class)
							.addPropertyReference("stringValueConverter", BeanNames.STRING_VALUE_CONVERTER)
							.setInitMethodName("initializeArgumentResolvers").getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_HANDLER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_WEB_SCRIPT_HANDLER,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationBasedWebScriptHandler.class)
							.addPropertyReference("handlerMethodArgumentsResolver",
									BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationBasedWebScriptBuilder.class)
							.addPropertyReference("annotationBasedWebScriptHandler",
									BeanNames.ANNOTATION_BASED_WEB_SCRIPT_HANDLER).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRY) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRY,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationBasedWebScriptRegistry.class)
							.addPropertyReference("annotationBasedWebScriptBuilder",
									BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(BeanNames.COMPOSITE_REGISTRY_MANAGER) == false) {
			beanFactory.registerBeanDefinition(
					BeanNames.COMPOSITE_REGISTRY_MANAGER,
					BeanDefinitionBuilder.rootBeanDefinition(CompositeRegistryManager.class)
							.addPropertyValue("compositeRegistry", getService(CompositeRegistry.class))
							.addPropertyReference("registries", BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRY)
							.setInitMethodName("registerRegistries").setDestroyMethodName("unregisterRegistries")
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

	/**
	 * Obtais the Java packages that exported by the Bundle.
	 * <p>
	 * This implementation parses the raw bundle header using the {@link ManifestHeaderParser}.
	 * 
	 * @return
	 */
	protected String[] getBundleExportPackages() {
		String[] exportPackages = null;
		final String exportPackageHeader = getBundle().getHeaders().get(Constants.EXPORT_PACKAGE);
		if (StringUtils.hasText(exportPackageHeader)) {
			exportPackages = ManifestHeaderParser.parseExportedPackages(exportPackageHeader);
		}
		return exportPackages;
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

	/**
	 * Obtains the {@link NamespaceHandlerResolver} using a {@link ServiceReference}.
	 */
	protected NamespaceHandlerResolver getNamespaceHandlerResolver() {
		try {
			return (NamespaceHandlerResolver) getBundleContext().getService(
					getBundleContext().getServiceReferences(NamespaceHandlerResolver.class.getName(),
							HOST_APPLICATION_ALFRESCO_FILTER)[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtains the {@link EntityResolver} using a {@link ServiceReference}.
	 */
	protected EntityResolver getEntityResolver() {
		try {
			return (EntityResolver) getBundleContext().getService(
					getBundleContext().getServiceReferences(EntityResolver.class.getName(),
							HOST_APPLICATION_ALFRESCO_FILTER)[0]);
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/* Configuration */

	public void setModelLocationPattern(final String modelLocationPattern) {
		this.modelLocationPattern = modelLocationPattern;
	}

	protected String getModelLocationPattern() {
		return modelLocationPattern;
	}

	protected boolean hasXmlConfiguration() {
		return hasXmlConfiguration;
	}

}