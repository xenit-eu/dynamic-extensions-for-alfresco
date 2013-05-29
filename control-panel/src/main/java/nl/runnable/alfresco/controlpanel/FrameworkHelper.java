package nl.runnable.alfresco.controlpanel;

import nl.runnable.alfresco.annotations.RunAsSystem;
import nl.runnable.alfresco.annotations.Transactional;
import nl.runnable.alfresco.osgi.FrameworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FrameworkHelper {

	@Autowired
	private FrameworkService frameworkService;

	/**
	 * Restarts the Framework.
	 */
	@RunAsSystem
	@Transactional
	public void restartFramework() {
		frameworkService.restartFramework();
	}

}
