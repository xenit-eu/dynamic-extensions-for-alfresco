package com.github.dynamicextensionsalfresco.aop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;

/**
 * Creates proxies for using {@link Advisor}s that are marked as {@link DynamicExtensionsAdvisor}s.
 * 
 * @author Laurens Fridael
 * 
 */
@SuppressWarnings("serial")
public class DynamicExtensionsAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator {

	/* Main operations */

	/**
	 * Obtains {@link Advisor}s that implement {@link DynamicExtensionsAdvisor}.
	 */
	@Override
	protected List<Advisor> findCandidateAdvisors() {
		final List<Advisor> advisors = new ArrayList<Advisor>(super.findCandidateAdvisors());
		for (final Iterator<Advisor> it = advisors.iterator(); it.hasNext();) {
			final Advisor advisor = it.next();
			if (!(advisor instanceof DynamicExtensionsAdvisor)) {
				it.remove();
			}
		}
		return advisors;
	}

}
