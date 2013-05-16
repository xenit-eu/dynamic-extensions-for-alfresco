package nl.runnable.alfresco.aop;

import nl.runnable.alfresco.annotations.Transactional;

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
