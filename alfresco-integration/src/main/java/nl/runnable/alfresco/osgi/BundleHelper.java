package nl.runnable.alfresco.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Helper for {@link Bundle} operations. Takes care of logging and exception handling.
 * 
 * @author Laurens Fridael
 * 
 */
class BundleHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void startBundle(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");

		if (isFragmentBundle(bundle) == false) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Starting Bundle. ID: {}, Symbolic Name: {}, Version: {}",
							new Object[] { bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion() });
				}
				bundle.start();
			} catch (final BundleException e) {
				logger.warn("Error starting Bundle", e);
			}
		}
	}

	public void stopBundle(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");

		if (isFragmentBundle(bundle) == false) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Stopping Bundle. ID: {}, Symbolic Name: {}, Version: {}",
							new Object[] { bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion() });
				}
				bundle.stop();
			} catch (final BundleException e) {
				logger.warn("Error stopping Bundle", e);
			}
		}
	}

	public void restartBundle(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");

		if (isFragmentBundle(bundle) == false) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Restarting Bundle. ID: {}, Symbolic Name: {}, Version: {}",
							new Object[] { bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion() });
				}
				bundle.stop();
				bundle.start();
			} catch (final BundleException e) {
				logger.warn("Error restarting Bundle", e);
			}
		}
	}

	public boolean isFragmentBundle(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");
		return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
	}
}
