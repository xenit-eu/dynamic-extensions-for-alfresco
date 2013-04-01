package nl.runnable.alfresco.extensions;

import java.util.Date;

public class Installation {

	/**
	 * The date the extension was first installed.
	 */
	private Date firstInstalled;

	/**
	 * The version that was first installed.
	 */
	private String firstVersion;

	/**
	 * The date the extension was last installed.
	 */
	private Date lastInstalled;

	/**
	 * The version that was last installed.
	 */
	private String lastVersion;

	public Date getFirstInstalled() {
		return firstInstalled;
	}

	public void setFirstInstalled(final Date firstInstalled) {
		this.firstInstalled = firstInstalled;
	}

	public String getFirstVersion() {
		return firstVersion;
	}

	public void setFirstVersion(final String firstVersion) {
		this.firstVersion = firstVersion;
	}

	public Date getLastInstalled() {
		return lastInstalled;
	}

	public void setLastInstalled(final Date lastInstalled) {
		this.lastInstalled = lastInstalled;
	}

	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(final String lastVersion) {
		this.lastVersion = lastVersion;
	}

}
