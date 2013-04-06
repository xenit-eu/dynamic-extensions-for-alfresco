package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Provides infrastructure for reading text configuration files.
 * 
 * @author Laurens Fridael
 * 
 * @param <T>
 */
public abstract class AbstractTextConfigurationFileFactoryBean<T> extends AbstractConfigurationFileFactoryBean<T> {

	private static final String DEFAULT_ENCODING = "utf-8";

	public static interface LineCallback {

		void doWithLine(String line);
	}

	/* Configuration */

	private String encoding = DEFAULT_ENCODING;

	/* Utility operations */

	protected void readConfigurations(final LineCallback lineCallback) throws IOException {
		for (final Resource configuration : resolveConfigurations()) {
			LineNumberReader in = null;
			try {
				in = new LineNumberReader(new InputStreamReader(configuration.getInputStream(), getEncoding()));
				for (String line; (line = in.readLine()) != null;) {
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					lineCallback.doWithLine(line);
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException e) {
					}
				}
			}
		}
	}

	/* Configuration */

	public void setEncoding(final String encoding) {
		Assert.hasText(encoding);
		this.encoding = encoding;
	}

	protected String getEncoding() {
		return encoding;
	}

}
