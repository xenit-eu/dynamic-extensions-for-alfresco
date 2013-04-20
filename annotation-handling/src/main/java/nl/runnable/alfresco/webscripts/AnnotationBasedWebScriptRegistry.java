package nl.runnable.alfresco.webscripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.JaxRSUriIndex;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.PackageDescriptionDocument;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.PathImpl;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.SchemaDescriptionDocument;
import org.springframework.extensions.webscripts.TypeDescription;
import org.springframework.extensions.webscripts.UriIndex;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Web Script {@link Registry} implementation that detects {@link AnnotationBasedWebScript}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationBasedWebScriptRegistry implements BeanFactoryAware, Registry {

	/* Dependencies */

	private AnnotationBasedWebScriptBuilder annotationBasedWebScriptBuilder;

	private ConfigurableListableBeanFactory beanFactory;

	/* State */

	private UriIndex uriIndex = new JaxRSUriIndex();

	private final List<AnnotationBasedWebScript> webScripts = new ArrayList<AnnotationBasedWebScript>();

	private final Map<String, AnnotationBasedWebScript> webScriptsById = new HashMap<String, AnnotationBasedWebScript>();

	private final Map<String, Path> packagesByPath = new HashMap<String, Path>();

	private final Map<String, Path> urisByPath = new HashMap<String, Path>();

	private final Map<String, Path> familiesByPath = new HashMap<String, Path>();

	private final Map<String, Path> lifecyclesByPath = new HashMap<String, Path>();

	/* Operations */

	protected synchronized void initializeAnnotationBasedWebScripts() {
		Assert.state(getBeanFactory() != null, "ApplicationContext has not been initialized yet.");

		clearIndexes();
		final AnnotationBasedWebScriptBuilder webScriptBuilder = getAnnotationBasedWebScriptBuilder();
		for (final String beanName : getBeanFactory().getBeanDefinitionNames()) {
			final List<AnnotationBasedWebScript> annotationBasedWebScripts = webScriptBuilder
					.createAnnotationBasedWebScripts(beanName);
			if (annotationBasedWebScripts != null) {
				webScripts.addAll(annotationBasedWebScripts);
			}
		}
		buildIndexes();
	}

	private void clearIndexes() {
		uriIndex.clear();
		webScripts.clear();
		webScriptsById.clear();
		packagesByPath.clear();
		packagesByPath.put("/", new PathImpl("/"));
		familiesByPath.clear();
		familiesByPath.put("/", new PathImpl("/"));
		urisByPath.clear();
		urisByPath.put("/", new PathImpl("/"));
		lifecyclesByPath.clear();
		lifecyclesByPath.put("/", new PathImpl("/"));
	}

	private void buildIndexes() {
		for (final AnnotationBasedWebScript webScript : webScripts) {
			final DescriptionImpl description = (DescriptionImpl) webScript.getDescription();
			webScriptsById.put(description.getId(), webScript);
			final String packageName = webScript.getHandler().getClass().getPackage().getName();
			final PathImpl packagePath = registerPathWithIndex(packageName.split("\\."), packagesByPath);
			packagePath.addScript(webScript);
			description.setPackage(packagePath);
			if (CollectionUtils.isEmpty(description.getFamilys()) == false) {
				for (final String family : description.getFamilys()) {
					registerPathWithIndex(family.split("/"), familiesByPath);
				}
			}
			for (String uri : description.getURIs()) {
				getUriIndex().registerUri(webScript, uri);
				// Chop off query part.
				if (uri.indexOf('?') > -1) {
					uri = uri.substring(0, uri.indexOf('?'));
				}
				registerPathWithIndex(uri.split("/"), urisByPath);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private PathImpl registerPathWithIndex(final String[] parts, final Map<String, Path> index) {
		final Map<String, PathImpl> map = (Map<String, PathImpl>) (Map<String, ?>) index;
		PathImpl path = map.get("/");
		for (final String part : parts) {
			final String packagePath;
			if (path.getPath().endsWith("/")) {
				packagePath = path.getPath() + part;
			} else {
				packagePath = path.getPath() + "/" + part;
			}
			if (map.containsKey(packagePath) == false) {
				path = path.createChildPath(part);
				map.put(packagePath, path);
			} else {
				path = map.get(packagePath);
			}
		}
		return path;
	}

	@Override
	public void reset() {
		initializeAnnotationBasedWebScripts();
	}

	@Override
	public WebScript getWebScript(final String id) {
		return webScriptsById.get(id);
	}

	@Override
	public Match findWebScript(final String method, final String uri) {
		return getUriIndex().findWebScript(method, uri);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<org.springframework.extensions.webscripts.WebScript> getWebScripts() {
		return (Collection<org.springframework.extensions.webscripts.WebScript>) ((List<?>) webScripts);
	}

	@Override
	public Path getPackage(final String packagePath) {
		return packagesByPath.get(packagePath);
	}

	@Override
	public Path getUri(final String uriPath) {
		return urisByPath.get(uriPath);
	}

	@Override
	public Path getFamily(final String familyPath) {
		return familiesByPath.get(familyPath);
	}

	@Override
	public Path getLifecycle(final String lifecyclePath) {
		return lifecyclesByPath.get(lifecyclePath);
	}

	/**
	 * Not supported, returns an empty Map.
	 */
	@Override
	public Map<String, String> getFailures() {
		return Collections.emptyMap();
	}

	/**
	 * Not supported, returns null.
	 */
	@Override
	public PackageDescriptionDocument getPackageDescriptionDocument(final String scriptPackage) {
		return null;
	}

	/**
	 * Not supported, returns null.
	 */
	@Override
	public SchemaDescriptionDocument getSchemaDescriptionDocument(final String schemaId) {
		return null;
	}

	/**
	 * Not supported, returns an empty List.
	 */
	@Override
	public Collection<PackageDescriptionDocument> getPackageDescriptionDocuments() {
		return Collections.emptyList();
	}

	/**
	 * Not supported, returns null.
	 */
	@Override
	public Collection<SchemaDescriptionDocument> getSchemaDescriptionDocuments() {
		return Collections.emptyList();
	}

	/**
	 * Not supported, returns null.
	 */
	@Override
	public TypeDescription getSchemaTypeDescriptionById(final String typeId) {
		return null;
	}

	/**
	 * Not supported, returns an empty Map.
	 */
	@Override
	public Map<String, String> getFailedPackageDescriptionsByPath() {
		return Collections.emptyMap();
	}

	/**
	 * Not supported, returns an empty Map.
	 */
	@Override
	public Map<String, String> getFailedSchemaDescriptionsByPath() {
		return Collections.emptyMap();
	}

	/* Dependencies */

	@Required
	public void setAnnotationBasedWebScriptBuilder(final AnnotationBasedWebScriptBuilder annotationBasedWebScriptBuilder) {
		Assert.notNull(annotationBasedWebScriptBuilder);
		this.annotationBasedWebScriptBuilder = annotationBasedWebScriptBuilder;
	}

	protected AnnotationBasedWebScriptBuilder getAnnotationBasedWebScriptBuilder() {
		return annotationBasedWebScriptBuilder;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
				"BeanFactory is not of type ConfigurableListableBeanFactory.");
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	protected ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	/* State */

	public void setUriIndex(final UriIndex uriIndex) {
		Assert.notNull(uriIndex);
		this.uriIndex = uriIndex;
	}

	protected UriIndex getUriIndex() {
		return uriIndex;
	}

}
