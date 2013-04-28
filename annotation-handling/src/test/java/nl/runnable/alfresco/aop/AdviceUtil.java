package nl.runnable.alfresco.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;

class AdviceUtil {

	static boolean hasAdvice(final Advised advised, final Class<? extends Advice> adviceType) {
		for (final Advisor advisor : advised.getAdvisors()) {
			if (adviceType.isInstance(advisor.getAdvice())) {
				return true;
			}
		}
		return false;
	}

	private AdviceUtil() {
	}

}
