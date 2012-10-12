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

package nl.runnable.alfresco.webscripts.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.PackageDescriptionDocument;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.SchemaDescriptionDocument;
import org.springframework.extensions.webscripts.TypeDescription;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;

/**
 * {@link CompositeRegistry} implementation that synchronizes the handling of its operations with the addition and
 * removal of {@link Registry} instances.
 * 
 * @author Laurens Fridael
 * 
 */
public class ConcurrentCompositeRegistry implements CompositeRegistry {

	/**
	 * Callback interface for internal use.
	 * 
	 * @author Laurens Fridael
	 * 
	 * @param <T>
	 */
	private static interface RegistriesOperation<T> {
		T doWithRegistries(final List<Registry> registries);
	}

	private final List<Registry> registries = new ArrayList<Registry>();

	private final ReadWriteLock registriesLock = new ReentrantReadWriteLock(true);

	@Override
	public void addRegistry(final Registry registry) {
		Assert.notNull(registry, "Registry cannot be null.");
		registriesLock.writeLock().lock();
		try {
			registries.add(registry);
		} finally {
			registriesLock.writeLock().unlock();
		}
	}

	@Override
	public void removeRegistry(final Registry registry) {
		Assert.notNull(registry, "Registry cannot be null.");
		registriesLock.writeLock().lock();
		try {
			registries.remove(registry);
		} finally {
			registriesLock.writeLock().unlock();
		}
	}

	protected <T> T performOperation(final RegistriesOperation<T> operation) {
		Assert.notNull(operation, "Operation cannot be null.");
		registriesLock.readLock().lock();
		try {
			return operation.doWithRegistries(registries);
		} finally {
			registriesLock.readLock().unlock();
		}
	}

	// Registry implementation

	@Override
	public Path getPackage(final String packagePath) {
		return performOperation(new RegistriesOperation<Path>() {

			@Override
			public Path doWithRegistries(final List<Registry> registries) {
				final CompositePath compositePath = new CompositePath();
				for (final Registry registry : registries) {
					final Path path = registry.getPackage(packagePath);
					if (path != null) {
						compositePath.addPath(path);
						break;
					}
				}
				return compositePath;
			}
		});
	}

	@Override
	public Path getUri(final String uriPath) {
		return performOperation(new RegistriesOperation<Path>() {

			@Override
			public Path doWithRegistries(final List<Registry> registries) {
				final CompositePath compositePath = new CompositePath();
				for (final Registry registry : registries) {
					final Path path = registry.getUri(uriPath);
					if (path != null) {
						compositePath.addPath(path);
						break;
					}
				}
				return compositePath;
			}
		});
	}

	@Override
	public Path getFamily(final String familyPath) {
		return performOperation(new RegistriesOperation<Path>() {

			@Override
			public Path doWithRegistries(final List<Registry> registries) {
				final CompositePath compositePath = new CompositePath();
				for (final Registry registry : registries) {
					final Path path = registry.getFamily(familyPath);
					if (path != null) {
						compositePath.addPath(path);
						break;
					}
				}
				return compositePath;
			}
		});
	}

	@Override
	public Path getLifecycle(final String lifecyclePath) {
		return performOperation(new RegistriesOperation<Path>() {

			@Override
			public Path doWithRegistries(final List<Registry> registries) {
				final CompositePath compositePath = new CompositePath();
				for (final Registry registry : registries) {
					final Path path = registry.getLifecycle(lifecyclePath);
					if (path != null) {
						compositePath.addPath(path);
						break;
					}
				}
				return compositePath;
			}
		});
	}

	@Override
	public Collection<WebScript> getWebScripts() {
		return performOperation(new RegistriesOperation<Collection<WebScript>>() {

			@Override
			public Collection<WebScript> doWithRegistries(final List<Registry> registries) {
				final List<WebScript> allWebScripts = new ArrayList<WebScript>();
				for (final Registry registry : registries) {
					final Collection<WebScript> registryWebScripts = registry.getWebScripts();
					if (registryWebScripts != null) {
						for (final WebScript webScript : registryWebScripts) {
							if (allWebScripts.contains(webScript) == false) {
								allWebScripts.add(webScript);
							}
						}
					}
				}
				return allWebScripts;
			}
		});
	}

	@Override
	public Map<String, String> getFailures() {
		return performOperation(new RegistriesOperation<Map<String, String>>() {
			@Override
			public Map<String, String> doWithRegistries(final List<Registry> registries) {
				final Map<String, String> allFailures = new HashMap<String, String>();
				for (final Registry registry : registries) {
					final Map<String, String> registryFailures = registry.getFailures();
					if (registryFailures != null) {
						for (final Map.Entry<String, String> entry : registryFailures.entrySet()) {
							if (allFailures.containsKey(entry.getKey()) == false) {
								allFailures.put(entry.getKey(), entry.getValue());
							}
						}
					}

				}
				return allFailures;
			}
		});
	}

	@Override
	public WebScript getWebScript(final String id) {
		return performOperation(new RegistriesOperation<WebScript>() {

			@Override
			public WebScript doWithRegistries(final List<Registry> registries) {
				WebScript webScript = null;
				for (final Registry registry : registries) {
					webScript = registry.getWebScript(id);
					if (webScript != null) {
						break;
					}
				}
				return webScript;
			}
		});
	}

	@Override
	public Match findWebScript(final String method, final String uri) {
		return performOperation(new RegistriesOperation<Match>() {

			@Override
			public Match doWithRegistries(final List<Registry> registries) {
				Match match = null;
				for (final Registry registry : registries) {
					match = registry.findWebScript(method, uri);
					if (match != null) {
						break;
					}
				}
				return match;
			}
		});
	}

	@Override
	public void reset() {
		performOperation(new RegistriesOperation<Void>() {

			@Override
			public Void doWithRegistries(final List<Registry> registries) {
				for (final Registry registry : registries) {
					registry.reset();
				}
				return null;
			}
		});
	}

	@Override
	public PackageDescriptionDocument getPackageDescriptionDocument(final String scriptPackage) {
		return performOperation(new RegistriesOperation<PackageDescriptionDocument>() {

			@Override
			public PackageDescriptionDocument doWithRegistries(final List<Registry> registries) {
				PackageDescriptionDocument packageDescriptionDocument = null;
				for (final Registry registry : registries) {
					packageDescriptionDocument = registry.getPackageDescriptionDocument(scriptPackage);
					if (packageDescriptionDocument != null) {
						break;
					}
				}
				return packageDescriptionDocument;
			}
		});
	}

	@Override
	public SchemaDescriptionDocument getSchemaDescriptionDocument(final String schemaId) {
		return performOperation(new RegistriesOperation<SchemaDescriptionDocument>() {

			@Override
			public SchemaDescriptionDocument doWithRegistries(final List<Registry> registries) {
				SchemaDescriptionDocument schemaDescriptionDocument = null;
				for (final Registry registry : registries) {
					schemaDescriptionDocument = registry.getSchemaDescriptionDocument(schemaId);
					if (schemaDescriptionDocument != null) {
						break;
					}
				}
				return schemaDescriptionDocument;
			}
		});
	}

	@Override
	public Collection<PackageDescriptionDocument> getPackageDescriptionDocuments() {
		return performOperation(new RegistriesOperation<Collection<PackageDescriptionDocument>>() {

			@Override
			public Collection<PackageDescriptionDocument> doWithRegistries(final List<Registry> registries) {
				final List<PackageDescriptionDocument> allPackageDescriptionDocuments = new ArrayList<PackageDescriptionDocument>();
				for (final Registry registry : registries) {
					final Collection<PackageDescriptionDocument> registryPackageDescriptionDocuments = registry
							.getPackageDescriptionDocuments();
					if (registryPackageDescriptionDocuments != null) {
						for (final PackageDescriptionDocument packageDescriptionDocument : registryPackageDescriptionDocuments) {
							if (allPackageDescriptionDocuments.contains(packageDescriptionDocument) == false) {
								allPackageDescriptionDocuments.add(packageDescriptionDocument);
							}
						}
					}
				}
				return allPackageDescriptionDocuments;
			}
		});
	}

	@Override
	public Collection<SchemaDescriptionDocument> getSchemaDescriptionDocuments() {
		return performOperation(new RegistriesOperation<Collection<SchemaDescriptionDocument>>() {

			@Override
			public Collection<SchemaDescriptionDocument> doWithRegistries(final List<Registry> registries) {
				final List<SchemaDescriptionDocument> allSchemaDescriptionDocuments = new ArrayList<SchemaDescriptionDocument>();
				for (final Registry registry : registries) {
					final Collection<SchemaDescriptionDocument> registrySchemaDescriptionDocuments = registry
							.getSchemaDescriptionDocuments();
					if (registrySchemaDescriptionDocuments != null) {
						for (final SchemaDescriptionDocument schemaDescriptionDocument : registrySchemaDescriptionDocuments) {
							if (allSchemaDescriptionDocuments.contains(schemaDescriptionDocument) == false) {
								allSchemaDescriptionDocuments.add(schemaDescriptionDocument);
							}
						}
					}
				}
				return allSchemaDescriptionDocuments;
			}
		});
	}

	@Override
	public TypeDescription getSchemaTypeDescriptionById(final String typeId) {
		return performOperation(new RegistriesOperation<TypeDescription>() {

			@Override
			public TypeDescription doWithRegistries(final List<Registry> registries) {
				TypeDescription typeDescription = null;
				for (final Registry registry : registries) {
					typeDescription = registry.getSchemaTypeDescriptionById(typeId);
					if (typeDescription != null) {
						break;
					}
				}
				return typeDescription;
			}
		});
	}

	@Override
	public Map<String, String> getFailedPackageDescriptionsByPath() {
		return performOperation(new RegistriesOperation<Map<String, String>>() {

			@Override
			public Map<String, String> doWithRegistries(final List<Registry> registries) {
				final Map<String, String> allFailedPackageDescriptions = new HashMap<String, String>();
				for (final Registry registry : registries) {
					final Map<String, String> registryFailedPackageDescriptionsByPath = registry
							.getFailedPackageDescriptionsByPath();
					if (registryFailedPackageDescriptionsByPath != null) {
						for (final Map.Entry<String, String> entry : registryFailedPackageDescriptionsByPath.entrySet()) {
							if (allFailedPackageDescriptions.containsKey(entry.getKey()) == false) {
								allFailedPackageDescriptions.put(entry.getKey(), entry.getValue());
							}
						}
					}

				}
				return allFailedPackageDescriptions;
			}
		});
	}

	@Override
	public Map<String, String> getFailedSchemaDescriptionsByPath() {
		return performOperation(new RegistriesOperation<Map<String, String>>() {

			@Override
			public Map<String, String> doWithRegistries(final List<Registry> registries) {
				final Map<String, String> allFailedSchemaDescriptions = new HashMap<String, String>();
				for (final Registry registry : registries) {
					final Map<String, String> registryFailedSchemaDescriptionsByPath = registry
							.getFailedSchemaDescriptionsByPath();
					if (registryFailedSchemaDescriptionsByPath != null) {
						for (final Map.Entry<String, String> entry : registryFailedSchemaDescriptionsByPath.entrySet()) {
							if (allFailedSchemaDescriptions.containsKey(entry.getKey()) == false) {
								allFailedSchemaDescriptions.put(entry.getKey(), entry.getValue());
							}
						}
					}
				}
				return allFailedSchemaDescriptions;
			}
		});
	}

}
