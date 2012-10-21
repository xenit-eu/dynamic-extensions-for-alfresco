package nl.runnable.alfresco.metadata;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.service.namespace.QName;

/**
 * Provides metadata on a Dynamic Extension and its contents.
 * 
 * @author Laurens Fridael
 * 
 */
public class ExtensionMetadata {

	/* Dependencies */

	private MetadataRegistry metadataRegistry;

	/* State */

	private long bundleId;

	private String name;

	private String version;

	private Date createdAt;

	private final Map<QName, ModelMetadata> modelsByQName = new LinkedHashMap<QName, ModelMetadata>();

	/* Dependencies */

	public void setMetadataRegistry(final MetadataRegistry metadataRegistry) {
		this.metadataRegistry = metadataRegistry;
	}

	protected MetadataRegistry getMetadataRegistry() {
		return metadataRegistry;
	}

	/* State */

	public void setBundleId(final long bundleId) {
		this.bundleId = bundleId;
	}

	public long getBundleId() {
		return bundleId;
	}

	public boolean isCoreBundle() {
		boolean coreBundle = false;
		if (getMetadataRegistry() != null) {
			coreBundle = getMetadataRegistry().isCoreBundle(getBundleId());
		}
		return coreBundle;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void registerModel(final QName qName, final ModelMetadata model) {
		modelsByQName.put(qName, model);
	}

	public void unregisterModel(final QName qName) {
		modelsByQName.remove(qName);
	}

	public Collection<ModelMetadata> getModels() {
		return modelsByQName.values();
	}

	/* Lifecycle */

	public void register() {
		getMetadataRegistry().registerExtension(this);
	}

	public void unregister() {
		getMetadataRegistry().unregisterExtension(this);
	}

	/* Equality */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ExtensionMetadata other = (ExtensionMetadata) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}

}
