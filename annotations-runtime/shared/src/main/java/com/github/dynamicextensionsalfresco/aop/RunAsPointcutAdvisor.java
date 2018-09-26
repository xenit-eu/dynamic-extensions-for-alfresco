package com.github.dynamicextensionsalfresco.aop;

import com.github.dynamicextensionsalfresco.annotations.RunAs;

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
