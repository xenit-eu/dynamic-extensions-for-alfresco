package nl.runnable.alfresco.aop;

import nl.runnable.alfresco.annotations.RunAs;
import nl.runnable.alfresco.annotations.RunAsSystem;
import nl.runnable.alfresco.annotations.Transactional;

public class ExampleBean {

	@Transactional
	@RunAs("admin")
	public void doWithDefaultSettings() {
	}

	@Transactional(readOnly = true)
	public void doWithReadOnly() {
	}

	@Transactional(readOnly = true, requiresNew = true)
	@RunAsSystem
	public void doWithReadOnlyAndRequiresNew() {
	}
}