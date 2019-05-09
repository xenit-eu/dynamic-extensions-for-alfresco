package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;

/**
 * Override Configuration to force synchronous startup
 *
 * @author Laurent Van der Linden
 */
public class SynchronousApplicationContextConfiguration extends ApplicationContextConfiguration {
    public SynchronousApplicationContextConfiguration(Bundle bundle) {
        super(bundle);
    }

    @Override
    public boolean isCreateAsynchronously() {
        return false;
    }
}
