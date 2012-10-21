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
import java.util.Date;

import nl.runnable.alfresco.actions.AnnotationBasedActionRegistrar;
import nl.runnable.alfresco.metadata.ExtensionMetadata;
import nl.runnable.alfresco.policy.AnnotationBasedBehaviourRegistrar;
import nl.runnable.alfresco.policy.DefaultBehaviourProxyFactory;
import nl.runnable.alfresco.policy.ProxyPolicyComponentFactoryBean;
import nl.runnable.alfresco.repository.dictionary.M2ModelListFactoryBean;
import nl.runnable.alfresco.repository.dictionary.ModelRegistrar;
import nl.runnable.alfresco.webscripts.AnnotationBasedWebScriptBuilder;
import nl.runnable.alfresco.webscripts.AnnotationBasedWebScriptRegistry;
import nl.runnable.alfresco.webscripts.integration.CompositeRegistry;
import nl.runnable.alfresco.webscripts.integration.CompositeRegistryManager;
import nl.runnable.alfresco.webscripts.integration.SearchPathRegistry;
import nl.runnable.alfresco.webscripts.integration.SearchPathRegistryManager;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Bundle;
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

	private static final String SEARCH_PATH_REGISTRY_MANAGER_BEAN_NAME = "searchPathRegistryManager";

	private static final String ANNOTATION_BASED_WEB_SCRIPT_BUILDER_BEAN_NAME = "annotationBasedWebScriptBuilder";

	private static final String ANNOTATION_BASED_WEB_SCRIPT_REGISTRY_BEAN_NAME = "annotationBasedWebScriptRegistry";

	private static final String COMPOSITE_REGISTRY_MANAGER_BEAN_NAME = "compositeRegistryManager";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String ANNOTATION_BASED_ACTION_REGISTRAR_BEAN_NAME = "annotationBasedActionRegistrar";

	private static final String ANNOTATION_BASED_BEHAVIOUR_REGISTRAR_BEAN_NAME = "annotationBasedBehaviourRegistrar";

	private static final String M2_MODEL_LIST_FACTORY_BEAN_NAME = "m2ModelListFactoryBean";

	private static final String MODEL_REGISTRAR_BEAN_NAME = "modelRegistrar";

	private static final String METADATA_BEAN_NAME = "metadata";

	private static final String PROXY_POLICY_COMPONENT_BEAN_NAME = "proxyPolicyComponent";

	private static final String BEHAVIOUR_PROXY_FACTORY_BEAN_NAME = "behaviourProxyFactory";

	private static final String HOST_APPLICATION_ALFRESCO_FILTER = "(hostApplication=alfresco)";

	/* Configuration */

	private String modelLocationPattern;

	private final boolean hasXmlConfiguration;

	/* Operations */

	DynamicExtensionsApplicationContext(final String[] configurationLocations) {
		super(configurationLocations);
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
		registerMetadataBean(beanFactory);
		if (StringUtils.hasText(getModelLocationPattern())) {
			registerAutomaticModelDeploymentBeans(beanFactory);
		}
		registerAnnotationBasedBehaviourBeans(beanFactory);
		registerAnnotationBasedActionBeans(beanFactory);
		registerAnnotationBasedWebScriptBeans(beanFactory);
	}

	/**
	 * Registers the {@link ExtensionMetadata} bean for recording information on the dynamic extensions.
	 * <p>
	 * This implementation initializes the bean's properties with settings from the extension's {@link Bundle}.
	 * 
	 * @param beanFactory
	 */
	protected void registerMetadataBean(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(METADATA_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					METADATA_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(ExtensionMetadata.class)
							.addPropertyValue("bundleId", getBundle().getBundleId())
							.addPropertyValue("name", getBundle().getSymbolicName())
							.addPropertyValue("version", getBundle().getVersion().toString())
							.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE).setInitMethodName("register")
							.setDestroyMethodName("unregister").getBeanDefinition());
		}
	}

	/**
	 * Registers the infrastructure beans necessary for automatic XML content model deployment.
	 * 
	 * @param beanFactory
	 */
	protected void registerAutomaticModelDeploymentBeans(final DefaultListableBeanFactory beanFactory) {
		if (beanFactory.containsBeanDefinition(M2_MODEL_LIST_FACTORY_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					M2_MODEL_LIST_FACTORY_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(M2ModelListFactoryBean.class)
							.addPropertyValue("locationPattern", getModelLocationPattern()).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(MODEL_REGISTRAR_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					MODEL_REGISTRAR_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(ModelRegistrar.class)
							.addPropertyReference("models", M2_MODEL_LIST_FACTORY_BEAN_NAME)
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
		if (beanFactory.containsBeanDefinition(BEHAVIOUR_PROXY_FACTORY_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(BEHAVIOUR_PROXY_FACTORY_BEAN_NAME, BeanDefinitionBuilder
					.rootBeanDefinition(DefaultBehaviourProxyFactory.class).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(PROXY_POLICY_COMPONENT_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					PROXY_POLICY_COMPONENT_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(ProxyPolicyComponentFactoryBean.class)
							.addPropertyValue("policyComponent", getService(PolicyComponent.class))
							.addPropertyReference("behaviourProxyFactory", BEHAVIOUR_PROXY_FACTORY_BEAN_NAME)
							.getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(ANNOTATION_BASED_BEHAVIOUR_REGISTRAR_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					ANNOTATION_BASED_BEHAVIOUR_REGISTRAR_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(AnnotationBasedBehaviourRegistrar.class)
							.addPropertyReference("policyComponent", PROXY_POLICY_COMPONENT_BEAN_NAME)
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
		if (beanFactory.containsBeanDefinition(ANNOTATION_BASED_ACTION_REGISTRAR_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					ANNOTATION_BASED_ACTION_REGISTRAR_BEAN_NAME,
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
		if (beanFactory.containsBeanDefinition(ANNOTATION_BASED_WEB_SCRIPT_BUILDER_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(ANNOTATION_BASED_WEB_SCRIPT_BUILDER_BEAN_NAME, BeanDefinitionBuilder
					.rootBeanDefinition(AnnotationBasedWebScriptBuilder.class).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(ANNOTATION_BASED_WEB_SCRIPT_REGISTRY_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					ANNOTATION_BASED_WEB_SCRIPT_REGISTRY_BEAN_NAME,
					BeanDefinitionBuilder
							.rootBeanDefinition(AnnotationBasedWebScriptRegistry.class)
							.addPropertyReference("annotationBasedWebScriptBuilder",
									ANNOTATION_BASED_WEB_SCRIPT_BUILDER_BEAN_NAME).getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(COMPOSITE_REGISTRY_MANAGER_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					COMPOSITE_REGISTRY_MANAGER_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(CompositeRegistryManager.class)
							.addPropertyValue("compositeRegistry", getService(CompositeRegistry.class))
							.addPropertyReference("registries", ANNOTATION_BASED_WEB_SCRIPT_REGISTRY_BEAN_NAME)
							.setInitMethodName("registerRegistries").setDestroyMethodName("unregisterRegistries")
							.getBeanDefinition());
		}
		if (beanFactory.containsBeanDefinition(SEARCH_PATH_REGISTRY_MANAGER_BEAN_NAME) == false) {
			beanFactory.registerBeanDefinition(
					SEARCH_PATH_REGISTRY_MANAGER_BEAN_NAME,
					BeanDefinitionBuilder.rootBeanDefinition(SearchPathRegistryManager.class)
							.addPropertyValue("searchPathRegistry", getService(SearchPathRegistry.class))
							.addPropertyValue("stores", new BundleStore(getBundle()))
							.setInitMethodName("registerStores").setDestroyMethodName("unregisterStores")
							.getBeanDefinition());
		}
	}

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

	/**
	 * Creates a {@link ExtensionMetadata} metadata object using this application context's {@link Bundle}.
	 * 
	 * @return
	 */
	protected ExtensionMetadata createMetadata() {
		final Bundle bundle = getBundle();
		final ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setBundleId(bundle.getBundleId());
		metadata.setName(bundle.getSymbolicName());
		metadata.setVersion(bundle.getVersion().toString());
		metadata.setCreatedAt(new Date());
		return metadata;
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