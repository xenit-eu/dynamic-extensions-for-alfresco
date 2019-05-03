package com.github.dynamicextensionsalfresco.aop;

import com.github.dynamicextensionsalfresco.annotations.RunAs;
import com.github.dynamicextensionsalfresco.annotations.RunAsSystem;
import com.github.dynamicextensionsalfresco.annotations.Transactional;

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