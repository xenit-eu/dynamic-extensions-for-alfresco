package nl.runnable.alfresco.webscripts.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.AbstractRuntimeContainer;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.PackageDescriptionDocument;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.SchemaDescriptionDocument;
import org.springframework.extensions.webscripts.TypeDescription;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;

/**
 * {@link Registry} that delegates operation to multiple {@link Registry} instances in sequence. This allows an
 * {@link AbstractRuntimeContainer} instance to address multiple Registries when obtaining Web Script information.
 * (Since this module runs within an Alfresco repository, the relevant {@link AbstractRuntimeContainer} implementation
 * is <code>org.alfresco.repo.web.scripts.RepositoryContainer</code>.)
 * <p>
 * The implementation augments the operations from an earlier {@link Registry} with those from a later {@link Registry}.
 * It explicitly does not override these operations. (It returns the first available result from a method and if the
 * method returns a {@link Collection}, it combines the results, preferring the first available result in case there are
 * duplicates.)
 * 
 * @author Laurens Fridael
 * 
 */
public class AugmentingRegistry implements Registry {

	private final List<Registry> registries;

	/**
	 * Constructs an instance using the given {@link Registry} instances.
	 * 
	 * @param registries
	 */
	public AugmentingRegistry(final List<Registry> registries) {
		Assert.notEmpty(registries, "Registries cannot be empty.");
		this.registries = registries;
	}

	protected List<Registry> getRegistries() {
		return registries;
	}

	/* Registry implementation */

	@Override
	public Path getPackage(final String packagePath) {
		final CompositePath compositePath = new CompositePath();
		for (final Registry registry : getRegistries()) {
			final Path path = registry.getPackage(packagePath);
			if (path != null) {
				compositePath.addPath(path);
			}
		}
		return compositePath;
	}

	@Override
	public Path getUri(final String uriPath) {
		final CompositePath compositePath = new CompositePath();
		for (final Registry registry : getRegistries()) {
			final Path path = registry.getUri(uriPath);
			if (path != null) {
				compositePath.addPath(path);
				break;
			}
		}
		return compositePath;
	}

	@Override
	public Path getFamily(final String familyPath) {
		final CompositePath compositePath = new CompositePath();
		for (final Registry registry : getRegistries()) {
			final Path path = registry.getFamily(familyPath);
			if (path != null) {
				compositePath.addPath(path);
				break;
			}
		}
		return compositePath;
	}

	@Override
	public Path getLifecycle(final String lifecyclePath) {
		final CompositePath compositePath = new CompositePath();
		for (final Registry registry : getRegistries()) {
			final Path path = registry.getLifecycle(lifecyclePath);
			if (path != null) {
				compositePath.addPath(path);
				break;
			}
		}
		return compositePath;
	}

	@Override
	public Collection<WebScript> getWebScripts() {
		final List<WebScript> webScripts = new ArrayList<WebScript>();
		for (final Registry registry : getRegistries()) {
			for (final WebScript webScript : registry.getWebScripts()) {
				if (webScripts.contains(webScript) == false) {
					webScripts.add(webScript);
				}
			}
		}
		return webScripts;
	}

	@Override
	public Map<String, String> getFailures() {
		final Map<String, String> failures = new HashMap<String, String>();
		for (final Registry registry : getRegistries()) {
			for (final Map.Entry<String, String> entry : registry.getFailures().entrySet()) {
				if (failures.containsKey(entry.getKey()) == false) {
					failures.put(entry.getKey(), entry.getValue());
				}
			}

		}
		return failures;
	}

	@Override
	public WebScript getWebScript(final String id) {
		WebScript webScript = null;
		for (final Registry registry : getRegistries()) {
			webScript = registry.getWebScript(id);
			if (webScript != null) {
				break;
			}
		}
		return webScript;
	}

	@Override
	public Match findWebScript(final String method, final String uri) {
		Match match = null;
		for (final Registry registry : getRegistries()) {
			match = registry.findWebScript(method, uri);
			if (match != null) {
				break;
			}
		}
		return match;
	}

	@Override
	public void reset() {
		for (final Registry registry : getRegistries()) {
			registry.reset();
		}
	}

	@Override
	public PackageDescriptionDocument getPackageDescriptionDocument(final String scriptPackage) {
		PackageDescriptionDocument packageDescriptionDocument = null;
		for (final Registry registry : getRegistries()) {
			packageDescriptionDocument = registry.getPackageDescriptionDocument(scriptPackage);
			if (packageDescriptionDocument != null) {
				break;
			}
		}
		return packageDescriptionDocument;
	}

	@Override
	public SchemaDescriptionDocument getSchemaDescriptionDocument(final String schemaId) {
		SchemaDescriptionDocument schemaDescriptionDocument = null;
		for (final Registry registry : getRegistries()) {
			schemaDescriptionDocument = registry.getSchemaDescriptionDocument(schemaId);
			if (schemaDescriptionDocument != null) {
				break;
			}
		}
		return schemaDescriptionDocument;
	}

	@Override
	public Collection<PackageDescriptionDocument> getPackageDescriptionDocuments() {
		final List<PackageDescriptionDocument> packageDescriptionDocuments = new ArrayList<PackageDescriptionDocument>();
		for (final Registry registry : getRegistries()) {
			for (final PackageDescriptionDocument packageDescriptionDocument : registry
					.getPackageDescriptionDocuments()) {
				if (packageDescriptionDocuments.contains(packageDescriptionDocument) == false) {
					packageDescriptionDocuments.add(packageDescriptionDocument);
				}
			}
		}
		return packageDescriptionDocuments;
	}

	@Override
	public Collection<SchemaDescriptionDocument> getSchemaDescriptionDocuments() {
		final List<SchemaDescriptionDocument> schemaDescriptionDocuments = new ArrayList<SchemaDescriptionDocument>();
		for (final Registry registry : getRegistries()) {
			for (final SchemaDescriptionDocument schemaDescriptionDocument : registry.getSchemaDescriptionDocuments()) {
				if (schemaDescriptionDocuments.contains(schemaDescriptionDocument) == false) {
					schemaDescriptionDocuments.add(schemaDescriptionDocument);
				}
			}
		}
		return schemaDescriptionDocuments;
	}

	@Override
	public TypeDescription getSchemaTypeDescriptionById(final String typeId) {
		TypeDescription typeDescription = null;
		for (final Registry registry : getRegistries()) {
			typeDescription = registry.getSchemaTypeDescriptionById(typeId);
			if (typeDescription != null) {
				break;
			}
		}
		return typeDescription;
	}

	@Override
	public Map<String, String> getFailedPackageDescriptionsByPath() {
		final Map<String, String> failedPackageDescriptions = new HashMap<String, String>();
		for (final Registry registry : getRegistries()) {
			for (final Map.Entry<String, String> entry : registry.getFailedPackageDescriptionsByPath().entrySet()) {
				if (failedPackageDescriptions.containsKey(entry.getKey()) == false) {
					failedPackageDescriptions.put(entry.getKey(), entry.getValue());
				}
			}

		}
		return failedPackageDescriptions;
	}

	@Override
	public Map<String, String> getFailedSchemaDescriptionsByPath() {
		final Map<String, String> failedSchemaDescriptions = new HashMap<String, String>();
		for (final Registry registry : getRegistries()) {
			for (final Map.Entry<String, String> entry : registry.getFailedSchemaDescriptionsByPath().entrySet()) {
				if (failedSchemaDescriptions.containsKey(entry.getKey()) == false) {
					failedSchemaDescriptions.put(entry.getKey(), entry.getValue());
				}
			}

		}
		return failedSchemaDescriptions;
	}

}
