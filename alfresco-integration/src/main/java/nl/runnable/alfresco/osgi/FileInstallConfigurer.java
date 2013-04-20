package nl.runnable.alfresco.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Manages the configuration of the <a href="http://felix.apache.org/site/apache-felix-file-install.html">Felix File
 * Install</a> tool.
 * <p>
 * This class effectively adapts the File Install system property configuration settings to JavaBeans-style properties.
 * 
 * @author Laurens Fridael
 * 
 */
public class FileInstallConfigurer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private Configuration configuration;

	private Integer poll;

	private final List<String> directories = Collections.emptyList();

	private String filter;

	private boolean scanImmediately = false;

	/* Operations */

	public void configureSettings() {
		if (getPoll() != null) {
			System.setProperty("felix.fileinstall.poll", getPoll().toString());
		}
		final String directories = getDirectoriesAsCommaDelimitedString();
		if (directories.isEmpty() == false) {
			if (logger.isInfoEnabled()) {
				logger.info("Dynamic Extension bundles can be placed in the following directories: {}",
						StringUtils.collectionToDelimitedString(getDirectoriesAsAbsolutePaths(), ","));
			}
			System.setProperty("felix.fileinstall.dir", directories);
		}
		if (StringUtils.hasText(getFilter())) {
			System.setProperty("felix.fileinstall.filter", getFilter());
		}
		if (isScanImmediately()) {
			System.setProperty("felix.fileinstall.noInitialDelay", Boolean.valueOf(isScanImmediately()).toString());
		}
		/* We don't want to write config. */
		System.setProperty("felix.fileinstall.enableConfigSave", "false");
	}

	/* Utility operations */

	protected List<String> getDirectoriesAsAbsolutePaths() {
		final List<String> absolutePaths = new ArrayList<String>();
		for (final String directory : getDirectories()) {
			absolutePaths.add(new File(directory).getAbsolutePath());
		}
		return absolutePaths;
	}

	private String getDirectoriesAsCommaDelimitedString() {
		final List<String> directories = getDirectories();
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<String> it = directories.iterator(); it.hasNext();) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/* Configuration */

	public void setConfiguration(final Configuration configuration) {
		Assert.notNull(configuration);
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Integer getPoll() {
		return poll;
	}

	public void setPoll(final Integer poll) {
		this.poll = poll;
	}

	public List<String> getDirectories() {
		return Arrays.asList(getConfiguration().getBundleDirectory().getAbsolutePath());
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}

	public void setScanImmediately(final boolean scanImmediately) {
		this.scanImmediately = scanImmediately;
	}

	public boolean isScanImmediately() {
		return scanImmediately;
	}

}
