package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.TaskListener;

/**
 * Activiti workflow listener, that can be configured using a <b>componentId</b>.
 * The <b>componentId</b> will identify the {@link TaskListener} component.
 *
 * @author Laurent Van der Linden
 * @deprecated use {@link DelegateTaskListener} instead
 */
public class DelegateListener extends DelegateTaskListener {}
