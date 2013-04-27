package nl.runnable.alfresco.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.RunAs;

import org.aopalliance.aop.Advice;

public class RunAsAdviceResolver implements AdviceResolver {

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return RunAs.class;
	}

	@Override
	public Advice getAdvice(final Set<Method> methods) {
		return new RunAsAdvice(methods);
	}

}
