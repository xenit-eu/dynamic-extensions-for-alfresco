package com.github.dynamicextensionsalfresco.webscripts;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

/**
 * {@link WebScript} proxy for internal use by {@link WebScriptUriRegistry}.
 * <p>
 * This implementation wraps an existing {@link WebScript}.
 */
class WebScriptProxy implements WebScript {

	/* State */

	private WebScript webScript;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private Description description;

	/* Main operations */

	WebScriptProxy(final WebScript webScript) {
		setWebScript(webScript);
	}

	@Override
	public void init(final Container container, final Description description) {
		/* No-op */
	}

	@Override
	public void setURLModelFactory(final URLModelFactory urlModelFactory) {
		/* No-op */
	}

	@Override
	public Description getDescription() {
		lock.readLock().lock();
		try {
			return description;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ResourceBundle getResources() {
		lock.readLock().lock();
		try {
			return webScript.getResources();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException {
		lock.readLock().lock();
		try {
			webScript.execute(req, res);
		} finally {
			lock.readLock().unlock();
		}
	}

	/* State */

	/**
	 * Configures the {@link WebScript} that this proxy delegates to.
	 * 
	 * @param webScript
	 */
	public void setWebScript(final WebScript webScript) {
		Assert.notNull(webScript);
		lock.writeLock().lock();
		try {
			this.webScript = webScript;
			if (webScript.getDescription() != null) {
				description = webScript.getDescription();
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public WebScript getWrappedWebScript() {
		return webScript;
	}

}
