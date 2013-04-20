package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.config.ServerProperties;

/**
 * Simple {@link ServerProperties} implementation for testing purposes.
 * 
 * @author Laurens Fridael
 * 
 */
class SimpleServerProperties implements ServerProperties {

	private final String scheme;

	private final String hostName;

	private final Integer port;

	SimpleServerProperties(final String scheme, final String hostName, final int port) {
		this.scheme = scheme;
		this.hostName = hostName;
		this.port = port;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public Integer getPort() {
		return port;
	}

}
