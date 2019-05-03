package com.github.dynamicextensionsalfresco.aop;

import com.github.dynamicextensionsalfresco.annotations.Transactional;

import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;

/**
 * {@link PointcutAdvisor} that provides {@link Advice} to {@link Transactional}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class TransactionalPointcutAdvisor extends MethodAnnotationPointcutAdvisor {

	protected TransactionalPointcutAdvisor() {
		super(Transactional.class);
	}

}
