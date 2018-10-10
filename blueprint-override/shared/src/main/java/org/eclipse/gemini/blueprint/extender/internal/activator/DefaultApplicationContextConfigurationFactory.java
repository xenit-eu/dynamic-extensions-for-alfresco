package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;

/**
 * Replace this {@link ApplicationContextConfigurationFactory} so we can override the Configuration implementation
 *
 * @author Laurent Van der Linden
 */
public class DefaultApplicationContextConfigurationFactory implements ApplicationContextConfigurationFactory {
	public ApplicationContextConfiguration createConfiguration(Bundle bundle) {
		return new SynchronousApplicationContextConfiguration(bundle);
	}
}
