package com.github.dynamicextensionsalfresco.web

import com.github.dynamicextensionsalfresco.webscripts.DummyStore
import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler
import org.osgi.framework.BundleContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.extensions.webscripts.*
import java.util.*

/**
 * Webscript to server static web resources found in an extension
 * registered only if /META-INF/alfresco/web resources are found
 *
 * @author Laurent Van der Linden
 */
public class ResourceWebscript(private val bundleContext: BundleContext) : WebScript, AbstractBundleResourceHandler() {
    private val module = bundleContext.getBundle().getSymbolicName().toLowerCase()

    init {
        initContentTypes()
    }

    private val descriptionImpl = run {
        val url = "/$module/web/{path}"
        val id = "${module}-web"
        val descriptionImpl = DescriptionImpl(
                id,
                "staticWebResource$module",
                "static web resources for extension $module",
                url
        )
        descriptionImpl.setMethod("GET")
        descriptionImpl.setDefaultFormat("html")
        descriptionImpl.setFormatStyle(Description.FormatStyle.argument)
        descriptionImpl.setUris(arrayOf(url))
        descriptionImpl.setFamilys(setOf("static-web"))
        descriptionImpl.setStore(DummyStore())
        descriptionImpl.setRequiredAuthentication(Description.RequiredAuthentication.none)
        descriptionImpl.setRequiredTransactionParameters(with(TransactionParameters()) {
            setRequired(Description.RequiredTransaction.none)
            this
        })

        descriptionImpl
    }

    override fun getBundleContext(): BundleContext? {
        return bundleContext
    }

    override fun setURLModelFactory(urlModelFactory: URLModelFactory?) {}

    override fun init(container: Container?, description: Description?) {}

    override fun execute(req: WebScriptRequest, res: WebScriptResponse) {
        val path = req.getServiceMatch().getTemplateVars().get("path")

        handleResource(path, req, res)
    }

    override fun getBundleEntryPath(path: String?): String? {
        return "/META-INF/alfresco/web/$path"
    }

    override fun getDescription(): Description? {
        return descriptionImpl
    }

    override fun getResources(): ResourceBundle? {
        return null
    }
}