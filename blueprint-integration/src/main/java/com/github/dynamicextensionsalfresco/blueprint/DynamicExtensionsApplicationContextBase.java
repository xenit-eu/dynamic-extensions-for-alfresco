package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.BeanNames;
import com.github.dynamicextensionsalfresco.actions.AnnotationBasedActionRegistrar;
import com.github.dynamicextensionsalfresco.aop.DynamicExtensionsAdvisorAutoProxyCreator;
import com.github.dynamicextensionsalfresco.event.EventBus;
import com.github.dynamicextensionsalfresco.event.events.SpringContextException;
import com.github.dynamicextensionsalfresco.messages.MessagesRegistrar;
import com.github.dynamicextensionsalfresco.metrics.SpringTimer;
import com.github.dynamicextensionsalfresco.models.M2ModelResourceListProvider;
import com.github.dynamicextensionsalfresco.models.RepositoryModelRegistrar;
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistry;
import com.github.dynamicextensionsalfresco.osgi.webscripts.SearchPathRegistryManager;
import com.github.dynamicextensionsalfresco.policy.AnnotationBasedBehaviourRegistrar;
import com.github.dynamicextensionsalfresco.policy.DefaultBehaviourProxyFactory;
import com.github.dynamicextensionsalfresco.policy.ProxyPolicyComponentFactoryBean;
import com.github.dynamicextensionsalfresco.resources.DefaultBootstrapService;
import com.github.dynamicextensionsalfresco.resources.ResourceHelper;
import com.github.dynamicextensionsalfresco.schedule.ScheduledTaskRegistrar;
import com.github.dynamicextensionsalfresco.schedule.quartz2.Quartz2TaskScheduler;
import com.github.dynamicextensionsalfresco.web.WebResourcesRegistrar;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptBuilder;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRegistrar;
import com.github.dynamicextensionsalfresco.webscripts.MessageConverterRegistry;
import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry;
import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver;
import com.github.dynamicextensionsalfresco.webscripts.arguments.StringValueConverter;
import com.github.dynamicextensionsalfresco.workflow.WorkflowDefinitionRegistrar;
import com.github.dynamicextensionsalfresco.workflow.activiti.DefaultWorkflowTaskRegistry;
import com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistrar;
import com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistry;
import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import com.springsource.util.osgi.manifest.ImportedPackage;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.util.VersionNumber;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.EntityResolver;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ApplicationContext} for Dynamic Extensions.
 * <p>
 * <p>
 * This implementation populates the {@link BeanFactory} with {@link BeanDefinition}s by scanning for classes in packages listed in
 * the bundle's {@value #SPRING_CONFIGURATION_HEADER} header. This enables XML-free configuration.
 * <p>
 * <p>
 * Using annotation for configuring the ApplicationContext is the default and preferred option. (See below for notes on
 * configuring the ApplicationContext through XML.)
 * <p>
 * <p>
 * This class also registers infrastructure beans that facilitate annotation-based Behaviours, Actions and Web Scripts.
 * <p>
 * <p>
 * Creating ApplicationContexts using XML configuration
 * <p>
 * <p>
 * If Spring XML configuration is present in the classpath folder `/META-INF/spring` this implementation falls back on
 * creating an {@link ApplicationContext} by combining configuration from all XML files in this folder.
 * <p>
 * <p>
 * This implementation works around classloading issues for Spring XML [NamespaceHandler]s when *embedding* the
 * Blueprint container within an application that exposes Spring library classes through system packages. The main
 * difference from a more typical setup is that the Spring library classes are NOT loaded as OSGi bundles by the OSGi
 * framework, but through the embedding application's classloader.
 * <p>
 * <p>
 * Specifically, this class loads bean definitions by creating an {@link XmlBeanDefinitionReader} configured with a
 * {@link NamespaceHandlerResolver} and an {@link EntityResolver} obtained from the embedding application. This, in turn, causes the
 * Spring XML configuration to be handled by {@link NamespaceHandler}s from the Spring libraries bundled with Alfresco. These
 * services must have a `hostApplication` property that is set to the value "alfresco" for this to work.
 * <p>
 * <p>
 * The alternative would be to load the Spring libraries as OSGi bundles. (The Spring JARs are already OSGi-enabled.)
 * While this could be considered a cleaner approach, it has the disadvantage of loading the Spring libraries twice: in
 * both the embedding application and the OSGi container.
 *
 * @author Laurens Fridael
 * @author Toon Geens
 */

public abstract class DynamicExtensionsApplicationContextBase extends OsgiBundleXmlApplicationContext {

    private final static Logger log = LoggerFactory.getLogger(DynamicExtensionsApplicationContextBase.class);

    private final boolean hasXmlConfiguration;

    public static final String SPRING_CONFIGURATION_HEADER = "Alfresco-Spring-Configuration";
    public static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

    private static final String HOST_APPLICATION_ALFRESCO_FILTER = "(hostApplication=alfresco)";

    public DynamicExtensionsApplicationContextBase(String[] configurationLocations, ApplicationContext parent) {
        super(configurationLocations, parent);

        this.hasXmlConfiguration = !ObjectUtils.isEmpty(configurationLocations);
    }

    @NotNull
    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        VersionNumber version = this.getService(DescriptorService.class).getServerDescriptor().getVersionNumber();
        return createVersionSpecificBeanFactory(version);
    }

    protected abstract DefaultListableBeanFactory createVersionSpecificBeanFactory(VersionNumber version);

    @Override
    protected void loadBeanDefinitions(@NotNull DefaultListableBeanFactory beanFactory) throws IOException {
        if (beanFactory == null) {
            throw new IllegalArgumentException("beanFactory is null");
        }

        boolean isAlfrescoDynamicExtension = this.isAlfrescoDynamicExtension();
        if (this.hasSpringConfigurationHeader()) {
            if (this.hasXmlConfiguration() && log.isWarnEnabled()) {
                log.warn("Spring XML configuration at /META-INF/spring will be ignored due to the presence of the '"
                        + SPRING_CONFIGURATION_HEADER + "' header.");
            }

            this.scanPackages(beanFactory, this.getSpringConfigurationPackages());
        } else if (this.hasXmlConfiguration()) {
            try {
                super.loadBeanDefinitions(beanFactory);
            } catch (BeanDefinitionParsingException ex) {
                log.warn("Error parsing bean definitions.", ex);
            }
        } else if (isAlfrescoDynamicExtension) {
            this.scanPackages(beanFactory, this.getBundleExportPackages());
        }

        if (isAlfrescoDynamicExtension) {
            this.registerInfrastructureBeans(beanFactory);
        }

    }

    @Override
    protected void initBeanDefinitionReader(@NotNull XmlBeanDefinitionReader beanDefinitionReader) {
        if (beanDefinitionReader == null) {
            throw new IllegalArgumentException("beanDefinitionReader is null");
        }

        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setNamespaceHandlerResolver(
                new CompositeNamespaceHandlerResolver(
                        this.getOsgiNamespaceHandlerResolver(),
                        new DefaultNamespaceHandlerResolver(this.getClassLoader()),
                        this.getHostNamespaceHandlerResolver()
                ));
        beanDefinitionReader.setEntityResolver(
                new CompositeEntityResolver(
                        this.getOsgiEntityResolver(),
                        new DelegatingEntityResolver(this.getClassLoader()),
                        this.getHostEntityResolver()
                ));
    }

    @Override
    protected void cancelRefresh(@NotNull BeansException ex) {
        if (ex == null) {
            throw new IllegalArgumentException("ex is null");
        }
        super.cancelRefresh(ex);

        try {
            getService(EventBus.class).publish(new SpringContextException(this.getBundle(), ex));
        } catch (Exception bx) {
            log.error("Failed to broadcast Spring refresh failure", bx);
        }
    }

    private void scanPackages(DefaultListableBeanFactory beanFactory, String[] configurationPackages) {
        if (log.isWarnEnabled()) {
            logWarnScanningOfImports(configurationPackages);
        }

        if (configurationPackages != null) {
            Descriptor serverDescriptor = this.getService(DescriptorService.class).getServerDescriptor();
            AlfrescoPlatformBeanDefinitionScanner scanner = new AlfrescoPlatformBeanDefinitionScanner(beanFactory,
                    serverDescriptor);
            scanner.setResourceLoader(this);
            scanner.scan(configurationPackages);
        }
    }

    private void logWarnScanningOfImports(final String[] packagesToScan) {
        if (packagesToScan == null || packagesToScan.length == 0) {
            return;
        }

        BundleManifest bundleManifest = BundleManifestFactory.createBundleManifest(this.getBundle().getHeaders());
        final List<String> importedPackages =
                bundleManifest.getImportPackage().getImportedPackages().stream()
                        .map(ImportedPackage::getPackageName)
                        .flatMap(p -> recursifyPackage(p).stream())
                        .collect(Collectors.toList());

        if (importedPackages.isEmpty()) {
            return;
        }

        final List<String> violatingPackages = Arrays.stream(packagesToScan)
                .filter(importedPackages::contains)
                .collect(Collectors.toList());

        if (!violatingPackages.isEmpty()) {
            log.warn("Bundle: '{}' --> Package(s) '{}' will be scanned for Spring beans but these are imported packages "
                    + "(see 'Import-Package' MANIFEST header), this can cause several issues like e.g. "
                    + "unintended, duplicate registration of some beans.", getBundle().getSymbolicName(), violatingPackages);
        }
    }

    /**
     * "eu.xenit.test" -> [eu, eu.xenit, eu.xenit.test]
     */
    static List<String> recursifyPackage(final String s) {
        List<String> packagesRecursive = new ArrayList<>();

        final String[] packageParts = s.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (final String packagePart : packageParts) {
            sb.append(packagePart);
            packagesRecursive.add(sb.toString());
            sb.append(".");
        }

        return packagesRecursive;
    }

    /* Dependencies */

    protected <TService> TService getService(Class<TService> clazz) {
        return BundleUtils.getService(this.getBundleContext(), clazz);
    }

    protected <TService> TService getService(Class<TService> clazz, String filter) {
        return BundleUtils.getService(this.getBundleContext(), clazz, filter);
    }

    protected NamespaceHandlerResolver getOsgiNamespaceHandlerResolver() {
        return getService(NamespaceHandlerResolver.class, BundleUtils.createNamespaceFilter(this.getBundleContext()));
    }

    /**
     * Obtains the [NamespaceHandlerResolver] using a [ServiceReference].
     */
    protected NamespaceHandlerResolver getHostNamespaceHandlerResolver() {
        return getService(NamespaceHandlerResolver.class, HOST_APPLICATION_ALFRESCO_FILTER);
    }

    protected EntityResolver getOsgiEntityResolver() {
        return getService(EntityResolver.class, BundleUtils.createNamespaceFilter(this.getBundleContext()));
    }

    /**
     * Obtains the [EntityResolver] using a [ServiceReference].
     */
    protected EntityResolver getHostEntityResolver() {
        return getService(EntityResolver.class, HOST_APPLICATION_ALFRESCO_FILTER);
    }

    /**
     * Use the deprecated PackageAdmin to get the list of exported packages.
     */
    protected String[] getBundleExportPackages() {
        String exportPackageHeader = this.getBundle().getHeaders().get(Constants.EXPORT_PACKAGE);
        if (StringUtils.hasText(exportPackageHeader)) {
            PackageAdmin packageAdmin = getService(PackageAdmin.class);
            ExportedPackage[] packages = packageAdmin.getExportedPackages(this.getBundle());

            String[] pkgNames = new String[packages.length];
            for (int index = 0; index != packages.length; index++) {
                pkgNames[index] = packages[index].getName();
            }

            return pkgNames;
        }
        return new String[0];
    }

    protected boolean hasXmlConfiguration() {
        return hasXmlConfiguration;
    }

    protected final boolean hasSpringConfigurationHeader() {
        return this.getBundle().getHeaders().get(SPRING_CONFIGURATION_HEADER) != null;
    }

    protected final boolean isAlfrescoDynamicExtension() {
        return Boolean.valueOf(this.getBundle().getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
    }

    @Nullable
    protected final String[] getSpringConfigurationPackages() {
        String header = this.getBundle().getHeaders().get(SPRING_CONFIGURATION_HEADER);
        if (StringUtils.hasText(header)) {
            return header.split(",");
        } else {
            return null;
        }
    }

    /**
     * Registers infrastructure beans for additional services such as annotation-based Behaviours.
     */
    protected void registerInfrastructureBeans(DefaultListableBeanFactory beanFactory) {
        if (beanFactory == null) {
            throw new IllegalArgumentException("beanFactory is null");
        }

        Descriptor serverDescriptor = this.getService(DescriptorService.class).getServerDescriptor();

        registerContentSupportBeans(beanFactory);
        registerModelDeploymentBeans(beanFactory);
        registerWorkflowDeployment(beanFactory);
        registerMessagesDeployment(beanFactory);
        registerAnnotationBasedBehaviourBeans(beanFactory);
        registerAnnotationBasedActionBeans(beanFactory);
        registerAnnotationBasedWebScriptBeans(beanFactory);
        registerAopProxyBeans(beanFactory);
        registerWorkflowBeans(beanFactory);
        registerOsgiServiceBeans(beanFactory);
        registerTaskSchedulingBeans(beanFactory, serverDescriptor);
        registerMetrics(beanFactory);
        registerWebResources(beanFactory);
    }

    /**
     * Registers the infrastructure beans necessary for automatic XML content model deployment.
     */


    private void registerContentSupportBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.RESOURCE_HELPER, ResourceHelper.class, beanAutowireByType);
        this.bean(beanFactory, BeanNames.BOOTSTRAP_SERVICE, DefaultBootstrapService.class, beanAutowireByType);
    }

    private void registerModelDeploymentBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.M2_MODEL_LIST_FACTORY, M2ModelResourceListProvider.class);

        this.bean(beanFactory, BeanNames.MODEL_REGISTRAR, RepositoryModelRegistrar.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                        builder.setInitMethodName("registerModels");
                        builder.setDestroyMethodName("unregisterModels");
                    }
                });
    }

    private void registerWorkflowDeployment(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.WORKFLOW_DEFINITION_REGISTRAR, WorkflowDefinitionRegistrar.class,
                beanAutowireByType);
    }

    private void registerMessagesDeployment(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.MESSAGES_REGISTRAR, MessagesRegistrar.class, beanAutowireByType);
    }

    private void bean(@NotNull DefaultListableBeanFactory beanFactory, @NotNull BeanNames name,
                      @NotNull Class beanClass) {
        this.bean(beanFactory, name, beanClass, null);
    }

    private void bean(@NotNull BeanDefinitionRegistry beanFactory, @NotNull BeanNames name,
                      @NotNull Class beanClass, @Nullable BeanDefinitionBuilderCustomizer body) {
        if (beanFactory == null) {
            throw new IllegalArgumentException("beanFactory is null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass is null");
        }

        if (!beanFactory.containsBeanDefinition(name.id())) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
            if (body != null) {
                body.customize(beanDefinitionBuilder);
            }

            beanFactory.registerBeanDefinition(name.id(), beanDefinitionBuilder.getBeanDefinition());
        }
    }

    /**
     * Registers the infrastructure beans that facilitate annotation-based Behaviours.
     */
    private void registerAnnotationBasedBehaviourBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.BEHAVIOUR_PROXY_FACTORY, DefaultBehaviourProxyFactory.class,
                beanAutowireByType);
        this.bean(beanFactory, BeanNames.PROXY_POLICY_COMPONENT, ProxyPolicyComponentFactoryBean.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyReference("policyComponent", "policyComponent");
                        builder.addPropertyReference("behaviourProxyFactory", BeanNames.BEHAVIOUR_PROXY_FACTORY.id());
                    }
                });
        this.bean(beanFactory, BeanNames.ANNOTATION_BASED_BEHAVIOUR_REGISTRAR, AnnotationBasedBehaviourRegistrar.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyReference("policyComponent", BeanNames.PROXY_POLICY_COMPONENT.id());
                        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                        builder.setInitMethodName("bindBehaviours");
                    }
                });
    }

    /**
     * Registers the infrastructure beans necessary for annotation-based Actions.
     */
    private void registerAnnotationBasedActionBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.ANNOTATION_BASED_ACTION_REGISTRAR, AnnotationBasedActionRegistrar.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                        builder.setInitMethodName("registerAnnotationBasedActions");
                        builder.setDestroyMethodName("unregisterAnnotationBasedActions");
                    }
                });
    }

    /**
     * Registers the infrastructure beans for annotation-based Web Scripts.
     */
    private void registerAnnotationBasedWebScriptBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.STRING_VALUE_CONVERTER, StringValueConverter.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyValue("namespacePrefixResolver", getService(NamespacePrefixResolver.class));
                    }
                });

        this.bean(beanFactory, BeanNames.MESSAGE_CONVERTER_REGISTER, MessageConverterRegistry.class);
        this.bean(beanFactory, BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER, HandlerMethodArgumentsResolver.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyReference("stringValueConverter", BeanNames.STRING_VALUE_CONVERTER.id());
                        builder.addPropertyReference("messageConverterRegistry",
                                BeanNames.MESSAGE_CONVERTER_REGISTER.id());
                        builder.addPropertyValue("bundleContext", getBundleContext());
                        builder.setInitMethodName("initializeArgumentResolvers");
                    }
                });

        this.bean(beanFactory, BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER, AnnotationWebScriptBuilder.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyReference("handlerMethodArgumentsResolver",
                                BeanNames.HANDLER_METHOD_ARGUMENTS_RESOLVER.id());
                        builder.addPropertyReference("messageConverterRegistry",
                                BeanNames.MESSAGE_CONVERTER_REGISTER.id());
                    }
                });

        this.bean(beanFactory, BeanNames.ANNOTATION_BASED_WEB_SCRIPT_REGISTRAR, AnnotationWebScriptRegistrar.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyReference("annotationBasedWebScriptBuilder",
                                BeanNames.ANNOTATION_BASED_WEB_SCRIPT_BUILDER.id());
                        builder.addPropertyValue("webScriptUriRegistry", getService(WebScriptUriRegistry.class));
                        builder.setInitMethodName("registerWebScripts");
                        builder.setDestroyMethodName("unregisterWebScripts");
                    }
                });

        this.bean(beanFactory, BeanNames.SEARCH_PATH_REGISTRY_MANAGER, SearchPathRegistryManager.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addPropertyValue("searchPathRegistry", getService(SearchPathRegistry.class));
                        builder.addPropertyValue("stores", new BundleStore(getBundle()));
                        builder.addPropertyValue("templateProcessor", getService(TemplateProcessor.class));
                        builder.setInitMethodName("registerStores");
                        builder.setDestroyMethodName("unregisterStores");
                    }
                });

    }

    private void registerAopProxyBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.AUTO_PROXY_CREATOR, DynamicExtensionsAdvisorAutoProxyCreator.class);
    }

    private void registerWorkflowBeans(DefaultListableBeanFactory beanFactory) {
        try {
            WorkflowTaskRegistry.class.getClassLoader().loadClass("org.activiti.engine.delegate.JavaDelegate");
        } catch (Exception ignore) {
            // swallow
            return;
        }

        this.bean(beanFactory, BeanNames.TYPE_BASED_WORKFLOW_REGISTRAR, WorkflowTaskRegistrar.class,
                new BeanDefinitionBuilderCustomizer() {
                    @Override
                    public void customize(BeanDefinitionBuilder builder) {
                        builder.addConstructorArgReference("activitiBeanRegistry");
                        builder.addConstructorArgReference(DefaultWorkflowTaskRegistry.BEAN_NAME);
                    }
                });
    }

    private void registerOsgiServiceBeans(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.OSGI_SERVICE_REGISTRAR, OsgiServiceRegistrar.class);
    }

    void registerTaskSchedulingBeans(DefaultListableBeanFactory beanFactory, Descriptor serverDescriptor) {

        VersionNumber version = serverDescriptor.getVersionNumber();


        // From Alfresco 6.x, we
        this.bean(beanFactory, BeanNames.QUARTZ_TASK_SCHEDULER, Quartz2TaskScheduler.class, beanAutowireByName);

        this.bean(beanFactory, BeanNames.SCHEDULED_TASK_REGISTRAR, ScheduledTaskRegistrar.class, beanAutowireByType);
    }

    private void registerMetrics(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.METRICS_TIMER, SpringTimer.class, beanAutowireByType);
    }

    private void registerWebResources(DefaultListableBeanFactory beanFactory) {
        this.bean(beanFactory, BeanNames.RESOURCES_WEB, WebResourcesRegistrar.class, beanAutowireByType);
    }

    // @FunctionalInterface
    public interface BeanDefinitionBuilderCustomizer {

        void customize(BeanDefinitionBuilder builder);

    }

    public static BeanDefinitionBuilderCustomizer beanAutowireByType = new BeanDefinitionBuilderCustomizer() {
        @Override
        public void customize(BeanDefinitionBuilder builder) {
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    };

    public static BeanDefinitionBuilderCustomizer beanAutowireByName = new BeanDefinitionBuilderCustomizer() {
        @Override
        public void customize(BeanDefinitionBuilder builder) {
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        }
    };
}
