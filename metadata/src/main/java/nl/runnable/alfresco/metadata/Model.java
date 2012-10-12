package nl.runnable.alfresco.metadata;

/**
 * Provides metadata on a registered model.
 * 
 * @author Laurens Fridael
 */
public class Model {

	private String name;

	private String description;

	private String filename;

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

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

}
