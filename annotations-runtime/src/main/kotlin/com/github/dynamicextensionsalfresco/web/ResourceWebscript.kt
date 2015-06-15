package com.github.dynamicextensionsalfresco.web

import com.github.dynamicextensionsalfresco.webscripts.DummyStore
import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler
import org.osgi.framework.BundleContext
import org.springframework.extensions.webscripts.*
import java.util.ResourceBundle
import javax.servlet.http.HttpServletResponse

/**
 * Webscript to serve static web resources found in an extension
 * When requests are prefixed with symbolic-name/web, no cache headers are set,
 * with the /web-cached/$version prefix, cache is set to expire now + 1 year
 *
 * @author Laurent Van der Linden
 */
public class ResourceWebscript(private val bundleContext: BundleContext) : WebScript, AbstractBundleResourceHandler() {
    private val module = bundleContext.getBundle().getSymbolicName().replace(".", "-").toLowerCase()

    init {
        initContentTypes()
    }

    private val descriptionImpl = run {
        val uris = arrayOf("/$module/web/{path}", "/$module/web-cached/{version}/{path}")
        val id = "${module}-web"
        val descriptionImpl = DescriptionImpl(
                id,
                "staticWebResource$module",
                "static web resources for extension $module",
                uris.first()
        )
        descriptionImpl.setMethod("GET")
        descriptionImpl.setFormatStyle(Description.FormatStyle.argument)
        descriptionImpl.setUris(uris)
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

        if (path != null) {
            if (req.getServiceMatch().getPath().startsWith("/$module/web-cached/")) {
                setInfinateCache(res)
            }

            handleResource(path, req, res)
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        }
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