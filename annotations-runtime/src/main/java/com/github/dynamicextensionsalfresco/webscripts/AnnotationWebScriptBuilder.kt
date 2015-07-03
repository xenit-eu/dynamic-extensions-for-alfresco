package com.github.dynamicextensionsalfresco.webscripts

import com.github.dynamicextensionsalfresco.util.hasText
import com.github.dynamicextensionsalfresco.webscripts.annotations.*
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashSet
import java.util.LinkedHashSet

import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver

import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.extensions.webscripts.Description
import org.springframework.extensions.webscripts.Description.RequiredAuthentication
import org.springframework.extensions.webscripts.Description.RequiredTransaction
import org.springframework.extensions.webscripts.Description.TransactionCapability
import org.springframework.extensions.webscripts.DescriptionImpl
import org.springframework.extensions.webscripts.TransactionParameters
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils
import org.springframework.util.StringUtils
import kotlin.properties.Delegates

/**
 * Creates [AnnotationWebScript] instances from beans defined in a [BeanFactory].

 * @author Laurens Fridael
 * @author Laurent Van der Linden
 */
public class AnnotationWebScriptBuilder Autowired constructor(
		private val handlerMethodArgumentsResolver: HandlerMethodArgumentsResolver
	) : BeanFactoryAware {

    /* Dependencies */

    protected var beanFactory: ConfigurableListableBeanFactory by Delegates.notNull()
        private set

    private val trailingSlashExpression = "/$".toRegex()
    private val leadingSlashExpression = "^/".toRegex()

    /* Main operations */

    /**
     * Creates [AnnotationWebScript]s from a given named bean by scanning methods annotated with [Uri].

     * @param beanName
     * *
     * @return The [AnnotationWebScript] or null if the implementation does not consider the bean to be a handler
     * *         for an [AnnotationWebScript].
     */
    public fun createWebScripts(beanName: String): List<org.springframework.extensions.webscripts.WebScript> {
        Assert.hasText(beanName, "Bean name cannot be empty.")

        val beanFactory = beanFactory
        val beanType = beanFactory.getType(beanName) ?: return emptyList()
        val webScriptAnnotation = beanFactory.findAnnotationOnBean<WebScript>(beanName, javaClass<WebScript>()) ?: getDefaultWebScriptAnnotation()
        val baseUri = webScriptAnnotation.baseUri
        if (StringUtils.hasText(baseUri) && baseUri.startsWith("/") == false) {
            throw RuntimeException("@WebScript baseUri for class '$beanType' does not start with a slash: '$baseUri'")
        }

        val handlerMethods = HandlerMethods()
        ReflectionUtils.doWithMethods(beanType) { method: Method ->
            val before = AnnotationUtils.findAnnotation<Before>(method, javaClass<Before>())
            if (before != null) {
                if (AnnotationUtils.findAnnotation<Attribute>(method, javaClass<Attribute>()) != null || AnnotationUtils.findAnnotation<Uri>(method, javaClass<Uri>()) != null) {
                    throw RuntimeException("Cannot combine @Before, @Attribute and @Uri on a single method. Method: ${ClassUtils.getQualifiedMethodName(method)}")
                }
                handlerMethods.getBeforeMethods().add(method)
            }
        }
        ReflectionUtils.doWithMethods(beanType) { method: Method ->
            val attribute = AnnotationUtils.findAnnotation<Attribute>(method, javaClass<Attribute>())
            if (attribute != null) {
                if (AnnotationUtils.findAnnotation<Before>(method, javaClass<Before>()) != null || AnnotationUtils.findAnnotation<Uri>(method, javaClass<Uri>()) != null) {
                    throw RuntimeException(("Cannot combine @Before, @Attribute and @Uri on a single method. Method: ${ClassUtils.getQualifiedMethodName(method)}"))
                }
                if (method.getReturnType() == Void.TYPE) {
                    throw RuntimeException("@Attribute methods cannot have a void return type.")
                }
                handlerMethods.getAttributeMethods().add(method)
            }
        }
        ReflectionUtils.doWithMethods(beanType) { method: Method ->
            val exceptionHandler = AnnotationUtils.findAnnotation<ExceptionHandler>(method, javaClass<ExceptionHandler>())
            if (exceptionHandler != null) {
                if (AnnotationUtils.findAnnotation<Attribute>(method, javaClass<Attribute>()) != null || AnnotationUtils.findAnnotation<Before>(method, javaClass<Before>()) != null || AnnotationUtils.findAnnotation<Uri>(method, javaClass<Uri>()) != null) {
                    throw RuntimeException("Cannot combine @Before, @Attribute @ExceptionHandler or @Uri on a single method. Method: ${ClassUtils.getQualifiedMethodName(method)}")
                }
                handlerMethods.getExceptionHandlerMethods().add(ExceptionHandlerMethod(exceptionHandler, method))
            }
        }

        val webScripts = ArrayList<org.springframework.extensions.webscripts.WebScript>()
        ReflectionUtils.doWithMethods(beanType) { method: Method ->
            val uri = AnnotationUtils.findAnnotation<Uri>(method, javaClass<Uri>())
            if (uri != null) {
                val webScript = createWebScript(beanName, webScriptAnnotation, uri, handlerMethods.createForUriMethod(method))
                webScripts.add(webScript)
            }
        }

        val ids = HashSet<String>()
        for (webScript in webScripts) {
            val webscriptId = webScript.getDescription().getId()
            val notContained = ids.add(webscriptId)
            if (!notContained) {
                throw IllegalStateException("Duplicate Web Script ID \"" + webscriptId + "\" Make sure handler methods of annotation-based Web Scripts have unique names.")
            }
        }

        return webScripts
    }

    /* Utility operations */

    protected fun createWebScript(beanName: String, webScript: WebScript, uri: Uri, handlerMethods: HandlerMethods): AnnotationWebScript {
        val description = DescriptionImpl()
        if (webScript.defaultFormat.hasText()) {
            description.setDefaultFormat(webScript.defaultFormat)
        }
        val baseUri = webScript.baseUri
        handleHandlerMethodAnnotation(uri, handlerMethods.getUriMethod(), description, baseUri)
        handleTypeAnnotations(beanName, webScript, description)
        val id = "%s.%s.%s".format(generateId(beanName), handlerMethods.getUriMethod().getName(), description.getMethod().toLowerCase())
        description.setId(id)
        val handler = beanFactory.getBean(beanName)
        description.setStore(DummyStore())
        return createWebScript(description, handler, handlerMethods)
    }

    protected fun createWebScript(description: Description, handler: Any, handlerMethods: HandlerMethods): AnnotationWebScript {
        return AnnotationWebScript(description, handler, handlerMethods, handlerMethodArgumentsResolver)
    }

    protected fun handleHandlerMethodAnnotation(uri: Uri, method: Method, description: DescriptionImpl, baseUri: String) {
        Assert.notNull(uri, "Uri cannot be null.")
        Assert.notNull(method, "HttpMethod cannot be null.")
        Assert.notNull(description, "Description cannot be null.")

        val uris: Array<String>
        if (uri.value.size() > 0) {
            uris = uri.value.map { "${baseUri.replace(trailingSlashExpression, "")}/${it.replace(leadingSlashExpression, "")}" }.toTypedArray()
        } else if (StringUtils.hasText(baseUri)) {
            uris = arrayOf(baseUri.replace(trailingSlashExpression, ""))
        } else {
            throw RuntimeException(
                    "No value specified for @Uri on method '%s' and no base URI found for @WebScript on class."
                            .format(ClassUtils.getQualifiedMethodName(method))
            )
        }
        description.setUris(uris)
        /*
		 * For the sake of consistency we translate the HTTP method from the HttpMethod enum. This also shields us from
		 * changes in the HttpMethod enum names.
		 */
        description.setMethod(when (uri.method) {
            HttpMethod.GET -> "GET"
            HttpMethod.POST -> "POST"
            HttpMethod.PUT -> "PUT"
            HttpMethod.DELETE -> "DELETE"
            HttpMethod.OPTIONS -> "OPTIONS"
        })
        /*
		 * Idem dito for FormatStyle.
		 */
        description.setFormatStyle(when (uri.formatStyle) {
            FormatStyle.ANY -> Description.FormatStyle.any
            FormatStyle.ARGUMENT -> Description.FormatStyle.argument
            FormatStyle.EXTENSION -> Description.FormatStyle.extension
        })
        if (uri.defaultFormat.hasText()) {
            description.setDefaultFormat(uri.defaultFormat)
        }
        description.setMultipartProcessing(uri.multipartProcessing)

        val methodAuthentication = method.getAnnotation<Authentication>(javaClass<Authentication>())
        if (methodAuthentication != null) {
            handleAuthenticationAnnotation(methodAuthentication, description)
        }

        val methodTransaction = method.getAnnotation<Transaction>(javaClass<Transaction>())
        if (methodTransaction != null) {
            handleTransactionAnnotation(methodTransaction, description)
        }
    }

    protected fun handleTypeAnnotations(beanName: String, webScript: WebScript, description: DescriptionImpl) {
        handleWebScriptAnnotation(webScript, beanName, description)

        if (description.getRequiredAuthentication() == null) {
            var authentication = beanFactory.findAnnotationOnBean<Authentication>(beanName, javaClass<Authentication>())
                ?: getDefaultAuthenticationAnnotation()
            handleAuthenticationAnnotation(authentication, description)
        }

        if (description.getRequiredTransactionParameters() == null) {
            var transaction = beanFactory.findAnnotationOnBean<Transaction>(beanName, javaClass<Transaction>())
            if (transaction == null) {
                if (description.getMethod().equals("GET")) {
                    transaction = getDefaultReadonlyTransactionAnnotation()
                } else {
                    transaction = getDefaultReadWriteTransactionAnnotation()
                }
            }
            handleTransactionAnnotation(transaction, description)
        }

        val cache = beanFactory.findAnnotationOnBean<Cache>(beanName, javaClass<Cache>()) ?: getDefaultCacheAnnotation()
        handleCacheAnnotation(cache, beanName, description)

        description.setDescPath("")
    }

    protected fun handleWebScriptAnnotation(webScript: WebScript, beanName: String, description: DescriptionImpl) {
        Assert.notNull(webScript, "Annotation cannot be null.")
        Assert.hasText(beanName, "Bean name cannot be empty.")
        Assert.notNull(description, "Description cannot be null.")
        Assert.hasText(description.getMethod(), "Description method is not specified.")

        if (webScript.value.hasText()) {
            description.setShortName(webScript.value)
        } else {
            description.setShortName(generateShortName(beanName))
        }
        if (webScript.description.hasText()) {
            description.setDescription(webScript.description)
        } else {
            description.setDescription("Annotation-based WebScript for class %s".format(beanFactory.getType(beanName).getName()))
        }
        if (webScript.families.size() > 0) {
            description.setFamilys(LinkedHashSet(Arrays.asList(*webScript.families)))
        }
        description.setLifecycle(when (webScript.lifecycle) {
            Lifecycle.NONE -> Description.Lifecycle.none
            Lifecycle.DRAFT -> Description.Lifecycle.draft
            Lifecycle.DRAFT_PUBLIC_API -> Description.Lifecycle.draft_public_api
            Lifecycle.DEPRECATED -> Description.Lifecycle.deprecated
            Lifecycle.INTERNAL -> Description.Lifecycle.internal
            Lifecycle.PUBLIC_API -> Description.Lifecycle.public_api
            Lifecycle.SAMPLE -> Description.Lifecycle.sample
        })
    }

    protected fun handleAuthenticationAnnotation(authentication: Authentication, description: DescriptionImpl) {
        Assert.notNull(authentication, "Annotation cannot be null.")
        Assert.notNull(description, "Description cannot be null.")
        if (authentication.runAs.hasText()) {
            description.setRunAs(authentication.runAs)
        }
        description.setRequiredAuthentication(when (authentication.value) {
            AuthenticationType.NONE -> RequiredAuthentication.none
            AuthenticationType.GUEST -> RequiredAuthentication.guest
            AuthenticationType.USER -> RequiredAuthentication.user
            AuthenticationType.ADMIN -> RequiredAuthentication.admin
        })
    }

    protected fun handleTransactionAnnotation(transaction: Transaction, description: DescriptionImpl) {
        Assert.notNull(transaction, "Annotation cannot be null.")
        Assert.notNull(description, "Description cannot be null.")

        val transactionParameters = TransactionParameters()
        transactionParameters.setRequired(when (transaction.value) {
            TransactionType.NONE -> RequiredTransaction.none
            TransactionType.REQUIRED -> RequiredTransaction.required
            TransactionType.REQUIRES_NEW -> RequiredTransaction.requiresnew
        })
        if (transaction.readOnly) {
            transactionParameters.setCapability(TransactionCapability.readonly)
        } else {
            transactionParameters.setCapability(TransactionCapability.readwrite)
        }
        transactionParameters.setBufferSize(transaction.bufferSize)
        description.setRequiredTransactionParameters(transactionParameters)
    }

    protected fun handleCacheAnnotation(cache: Cache, beanName: String, description: DescriptionImpl) {
        Assert.notNull(cache, "Annotation cannot be null.")
        Assert.hasText(beanName, "Bean name cannot be empty.")
        Assert.notNull(description, "Description cannot be null.")

        val requiredCache = org.springframework.extensions.webscripts.Cache()
        requiredCache.setNeverCache(cache.neverCache)
        requiredCache.setIsPublic(cache.isPublic)
        requiredCache.setMustRevalidate(cache.mustRevalidate)
        description.setRequiredCache(requiredCache)
    }

    protected fun generateId(beanName: String): String {
        Assert.hasText(beanName, "Bean name cannot be empty")
        val clazz = beanFactory.getType(beanName)
        return clazz.getName()
    }

    protected fun generateShortName(beanName: String): String {
        Assert.hasText(beanName, "Bean name cannot be empty")
        val clazz = beanFactory.getType(beanName)
        return ClassUtils.getShortName(clazz)
    }

    /*
	 * These methods use local classes to obtain annotations with default settings.
	 */
    private fun getDefaultAuthenticationAnnotation(): Authentication {
        @Authentication
        class Default
        return javaClass<Default>().getAnnotation(javaClass<Authentication>())
    }

    private fun getDefaultReadWriteTransactionAnnotation(): Transaction {
        @Transaction
        class Default
        return javaClass<Default>().getAnnotation(javaClass<Transaction>())
    }

    private fun getDefaultReadonlyTransactionAnnotation(): Transaction {
        @Transaction(readOnly = true)
        class Default
        return javaClass<Default>().getAnnotation(javaClass<Transaction>())
    }

    private fun getDefaultCacheAnnotation(): Cache {
        @Cache
        class Default
        return javaClass<Default>().getAnnotation(javaClass<Cache>())
    }

    private fun getDefaultWebScriptAnnotation(): WebScript {
        @WebScript
        class Default
        return javaClass<Default>().getAnnotation(javaClass<WebScript>())
    }

    /* Dependencies */

    override fun setBeanFactory(beanFactory: BeanFactory) {
        Assert.isInstanceOf(javaClass<ConfigurableListableBeanFactory>(), beanFactory, "BeanFactory is not of type ConfigurableListableBeanFactory.")
        this.beanFactory = beanFactory as ConfigurableListableBeanFactory
    }
}
