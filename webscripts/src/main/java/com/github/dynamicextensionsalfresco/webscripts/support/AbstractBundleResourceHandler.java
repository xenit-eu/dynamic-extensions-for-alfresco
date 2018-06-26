package com.github.dynamicextensionsalfresco.webscripts.support;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.TemplateResolution;
import org.apache.http.HttpStatus;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for annotation-based Web Scripts that send static resources, such as Resources, CSS and images,
 * from OSGi {@link Bundle}s.
 * 
 * @author Laurens Fridael
 * 
 */
public abstract class AbstractBundleResourceHandler {

	/* Dependencies */

	@Autowired
	private BundleContext bundleContext;

	/* Container */

	private Map<String, String> contentTypesByExtension;

	private String defaultContentType = "application/octet-stream";

	/* Main operations */

	/**
	 * copy the resource if any from the classpath to the outputstream
	 * @deprecated replaced with {@link AbstractBundleResourceHandler#handleResource(String, WebScriptRequest, WebScriptResponse)}
	 * to enable directory listing
	 *
	 * @param path the path of the resource to handle
     * @param response the webscript response
     *
     * @throws IOException When copying the file fails
	 */
	protected final void handleResource(final String path, final WebScriptResponse response) throws IOException {
		try {
			handleResource(path, null, response);
		} catch (IOException iox) {
			throw iox;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * handle the requested path
     * @param path the path of the resource to handle
     * @param request the webscript request
     * @param response the webscript response
     *
     * @throws IOException When copying the file fails
	 */
	protected final void handleResource(final String path, final WebScriptRequest request, final WebScriptResponse response) throws Exception {
		final String entryPath = getBundleEntryPath(path.replace("//", "/")); // forgive double slashes
		final URL resource = getBundleContext().getBundle().getEntry(entryPath);
		if (resource != null) {
            if(resource.getPath().endsWith("/") && request != null) {
                sendDirectoryListing(request, response, resource);
            }
			else sendResource(request, response, resource);
		} else {
			handleResourceNotFound(path, response);
		}
	}

	/* Utility operations */

	/**
	 * copy the resource if any from the classpath to the outputstream
	 * @deprecated replaced with {@link AbstractBundleResourceHandler#sendResource(WebScriptRequest, WebScriptResponse, URL)}
     *
     * @param response the webscript response
     * @param resource  the url of the resource
     * @throws IOException When copying the file fails
	 */
	protected void sendResource(final WebScriptResponse response, final URL resource) throws IOException {
		sendResource(null, response, resource);
	}

	/**
	 *  copy the resource if any from the classpath to the outputstream
     *
     * @param request the webscript request
     * @param response the webscript response
     * @param resource  the url of the resource
     *
     * @throws IOException When copying the file fails
	 */
	protected void sendResource(final WebScriptRequest request, final WebScriptResponse response, final URL resource) throws IOException {
		final String contentType = getContentType(resource);
		response.setContentType(contentType);
		response.setContentEncoding(getContentEncoding(resource));
		final URLConnection connection = resource.openConnection();
		final long lastModified = getBundleContext().getBundle().getLastModified();
		response.setHeader("Last-Modified",
				new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH).format(new Date(lastModified)));
		final int contentLength = connection.getContentLength();
		if (contentLength > -1) {
			response.setHeader("Content-Length", String.valueOf(contentLength));
		}
		FileCopyUtils.copy(connection.getInputStream(), response.getOutputStream());
	}

	protected void sendDirectoryListing(final WebScriptRequest request, final WebScriptResponse response, final URL resource) throws Exception {
        Enumeration<String> entries = getBundleContext().getBundle().getEntryPaths(resource.getPath());
        List<String> paths = new ArrayList<String>();
        while(entries.hasMoreElements()){
            String entry = entries.nextElement();
			if (entry.endsWith("/")) {
				paths.add(entry.substring(entry.lastIndexOf('/', entry.length() - 2) + 1));
			} else {
				paths.add(entry.substring(entry.lastIndexOf('/') + 1));
			}
        }
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("paths", paths);
        Resolution resolution = new TemplateResolution("dynamicextensionsalfresco/directory-listing.html.ftl",model);
        resolution.resolve(
				new AnnotationWebScriptRequest(request),
				new AnnotationWebscriptResponse(response),
				null);
    }

	protected void handleResourceNotFound(final String path, final WebScriptResponse response) throws IOException {
		response.setStatus(HttpStatus.SC_NOT_FOUND);
		response.setContentType("text/html");
		final Writer out = response.getWriter();
		out.write(String.format("<!doctype html><head><title>Not found</title></head><body>Could not find resource at path '%s'.</body></html>", HtmlUtils.htmlEscape(path)));
		out.close();
	}

	protected String getBundleEntryPath(final String path) {
		return path;
	}

	protected String getContentType(final URL resource) {
		final Matcher matcher = Pattern.compile(".+\\.(\\w+)$").matcher(resource.getFile());
		final String extension = matcher.matches() ? matcher.group(1) : null;
		String contentType = null;
		if (extension != null) {
			contentType = getContentTypesByExtension().get(extension.toLowerCase());
		}
		if (contentType == null) {
			contentType = getDefaultContentType();
		}
		return contentType;
	}

    /**
     * Add long term cache headers for resources that are known not to change, ie. versioned filename.
     * @param response the webscript resonse
     */
    protected void setInfinateCache(final WebScriptResponse response) {
        final long future = new Date().getTime() + 31536000000L;
        response.setHeader("Expires",
            new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z").format(new Date(future)));
        response.setHeader("Cache-Control", "max-age=" + future);
    }

	@PostConstruct
	protected void initContentTypes() {
		if (contentTypesByExtension != null) {
			return;
		}
		contentTypesByExtension = new HashMap<String, String>();
		contentTypesByExtension.put("js", "text/javascript");
		contentTypesByExtension.put("map", "application/json");
		contentTypesByExtension.put("css", "text/css");
		contentTypesByExtension.put("json", "application/json");
		contentTypesByExtension.put("gif", "image/gif");
		contentTypesByExtension.put("png", "image/png");
		contentTypesByExtension.put("jpg", "image/jpeg");
        contentTypesByExtension.put("svg", "image/svg+xml");
		contentTypesByExtension.put("html", "text/html");
	}

	protected String getContentEncoding(final URL resource) {
		// For now, we assume utf-8.
		return "utf-8";
	}

	/* Dependencies */

	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	/* Container */

	public void setContentTypesByExtension(final Map<String, String> contentTypesByExtension) {
		Assert.notEmpty(contentTypesByExtension);
		this.contentTypesByExtension = contentTypesByExtension;
	}

	protected Map<String, String> getContentTypesByExtension() {
		return contentTypesByExtension;
	}

	public void setDefaultContentType(final String defaultContentType) {
		Assert.hasText(defaultContentType);
		this.defaultContentType = defaultContentType;
	}

	protected String getDefaultContentType() {
		return defaultContentType;
	}
}
