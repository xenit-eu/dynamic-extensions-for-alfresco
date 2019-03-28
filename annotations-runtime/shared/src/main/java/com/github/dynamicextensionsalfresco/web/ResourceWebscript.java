package com.github.dynamicextensionsalfresco.web;

import com.github.dynamicextensionsalfresco.webscripts.DummyStore;
import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.function.Function;
import javax.servlet.http.HttpServletResponse;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.TransactionParameters;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Webscript to serve static web resources found in an extension When requests are prefixed with symbolic-name/web, no
 * cache headers are set, with the /web-cached/$version prefix, cache is set to expire now + 1 year
 *
 * @author Laurent Van der Linden
 */
public final class ResourceWebscript extends AbstractBundleResourceHandler implements WebScript {

    @NotNull
    private final BundleContext bundleContext;
    private final String module;

    private Function<ResourceWebscript, Description> descriptionImpl = (ResourceWebscript r) -> {
        final String[] uris = new String[]{"/" + r.module + "/web/{path}",
                "/" + r.module + "/web-cached/{version}/{path}"};
        final String id = r.module + "-web";
        DescriptionImpl descriptionImpl = new DescriptionImpl(
                id,
                "staticWebResource" + r.module,
                "static web resources for extension " + r.module,
                uris[0]
        );
        descriptionImpl.setMethod("GET");
        descriptionImpl.setFormatStyle(Description.FormatStyle.argument);
        descriptionImpl.setDefaultFormat("html");
        descriptionImpl.setUris(uris);
        descriptionImpl.setFamilys(new HashSet<>(Collections.singletonList("static-web")));
        descriptionImpl.setStore(new DummyStore());
        descriptionImpl.setRequiredAuthentication(Description.RequiredAuthentication.none);
        TransactionParameters transactionParameters = new TransactionParameters();
        transactionParameters.setRequired(RequiredTransaction.none);
        descriptionImpl.setRequiredTransactionParameters(transactionParameters);

        return descriptionImpl;
    };

    public ResourceWebscript(@NotNull BundleContext bundleContext) {
        Intrinsics.checkParameterIsNotNull(bundleContext, "bundleContext");

        this.bundleContext = bundleContext;
        this.module = bundleContext.getBundle().getSymbolicName().replace(".", "-").toLowerCase();

        initContentTypes();
    }

    @Override
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public void setURLModelFactory(URLModelFactory urlModelFactory) {
    }

    @Override
    public void init(Container container, Description description) {
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String path = req.getServiceMatch().getTemplateVars().get("path");

        if (path != null) {
            if (req.getServiceMatch().getPath().startsWith("/" + module + "/web-cached/") && !shouldNotCache(path)) {
                setInfinateCache(res);
            }

            try {
                handleResource(path, req, res);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    private boolean shouldNotCache(@NotNull String path) {
        Intrinsics.checkParameterIsNotNull(bundleContext, "path");
        return path.endsWith(".map");
    }

    @Override
    @Nullable
    public String getBundleEntryPath(@Nullable String path) {
        return "/META-INF/alfresco/web/" + path;
    }

    @Override
    @Nullable
    public Description getDescription() {
        return descriptionImpl.apply(this);
    }

    @Override
    @Nullable
    public ResourceBundle getResources() {
        return null;
    }
}