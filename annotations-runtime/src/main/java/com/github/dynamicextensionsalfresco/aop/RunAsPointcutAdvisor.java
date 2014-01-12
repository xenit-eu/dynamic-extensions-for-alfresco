package nl.runnable.alfresco.aop;

import nl.runnable.alfresco.annotations.RunAs;

import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;

/**
 * {@link PointcutAdvisor} that provides {@link Advice} to {@link RunAs}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class RunAsPointcutAdvisor extends MethodAnnotationPointcutAdvisor {

	protected RunAsPointcutAdvisor() {
		super(RunAs.class);
	}

}
