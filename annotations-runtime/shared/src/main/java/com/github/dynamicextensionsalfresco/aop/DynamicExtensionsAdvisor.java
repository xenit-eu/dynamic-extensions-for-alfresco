package com.github.dynamicextensionsalfresco.aop;

import org.springframework.aop.Advisor;

/**
 * Marker interface for {@link Advisor}s detected by {@link DynamicExtensionsAdvisorAutoProxyCreator}. This interface
 * enables {@link DynamicExtensionsAdvisorAutoProxyCreator} to filter out regular {@link Advisor}s.
 * 
 * @author Laurens Fridael
 * 
 */
public interface DynamicExtensionsAdvisor {

}
