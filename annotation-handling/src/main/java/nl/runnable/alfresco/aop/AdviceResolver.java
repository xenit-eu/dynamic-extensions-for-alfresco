package nl.runnable.alfresco.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.aopalliance.aop.Advice;

/**
 * Defines operations for resolving AOP {@link Advice}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface AdviceResolver {

	/**
	 * Obtains the annotation type supported by this resolver.
	 * 
	 * @return
	 */
	Class<? extends Annotation> getAnnotationType();

	/**
	 * Provides advice for the given methods.
	 * 
	 * @param methods
	 * @return
	 */
	Advice getAdvice(Set<Method> methods);
}
