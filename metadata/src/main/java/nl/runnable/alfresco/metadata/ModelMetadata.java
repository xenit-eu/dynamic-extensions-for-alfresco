package nl.runnable.alfresco.metadata;

/**
 * Provides metadata on a registered model.
 * 
 * @author Laurens Fridael
 */
public class ModelMetadata {

	private String name;

	private String description;

	private String version;

	public String getName() {
		return name;
	}

	public void setName(final String title) {
		this.name = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

}
