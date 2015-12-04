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
    private val module = bundleContext.bundle.symbolicName.replace(".", "-").toLowerCase()

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
        descriptionImpl.method = "GET"
        descriptionImpl.formatStyle = Description.FormatStyle.argument
        descriptionImpl.defaultFormat = "html"
        descriptionImpl.setUris(uris)
        descriptionImpl.familys = setOf("static-web")
        descriptionImpl.store = DummyStore()
        descriptionImpl.requiredAuthentication = Description.RequiredAuthentication.none
        descriptionImpl.requiredTransactionParameters = with(TransactionParameters()) {
            required = Description.RequiredTransaction.none
            this
        }

        descriptionImpl
    }

    override fun getBundleContext(): BundleContext? {
        return bundleContext
    }

    override fun setURLModelFactory(urlModelFactory: URLModelFactory?) {}

    override fun init(container: Container?, description: Description?) {}

    override fun execute(req: WebScriptRequest, res: WebScriptResponse) {
        val path = req.serviceMatch.templateVars.get("path")

        if (path != null) {
            if (req.serviceMatch.path.startsWith("/$module/web-cached/") && !shouldNotCache(path)) {
                setInfinateCache(res)
            }

            handleResource(path, req, res)
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        }
    }

    private fun shouldNotCache(path: String) = path.endsWith(".map")

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