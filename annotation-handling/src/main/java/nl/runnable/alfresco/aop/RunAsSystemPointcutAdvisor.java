package nl.runnable.alfresco.aop;

import nl.runnable.alfresco.annotations.RunAsSystem;

import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;

/**
 * {@link PointcutAdvisor} that provides {@link Advice} to {@link RunAsSystem}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class RunAsSystemPointcutAdvisor extends MethodAnnotationPointcutAdvisor {

	protected RunAsSystemPointcutAdvisor() {
		super(RunAsSystem.class);
	}

}
