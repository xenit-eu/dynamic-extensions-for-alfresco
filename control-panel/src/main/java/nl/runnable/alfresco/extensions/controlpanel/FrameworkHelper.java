package nl.runnable.alfresco.extensions.controlpanel;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.annotations.RunAsSystem;
import nl.runnable.alfresco.annotations.Transactional;
import nl.runnable.alfresco.osgi.FrameworkService;

@ManagedBean
public class FrameworkHelper {

	@Inject
	private FrameworkService frameworkService;

	/**
	 * Restarts the Framework.
	 */
	@RunAsSystem
	@Transactional(readOnly = true)
	public void restartFramework() {
		frameworkService.restartFramework();
	}

}
