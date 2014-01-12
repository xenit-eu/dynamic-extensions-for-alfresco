package nl.runnable.alfresco.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a candiate for being spied using {@link SpyBeanPostProcessor}. This class acts as a workaround:
 * spying using a mocking library turns out to be an anti-pattern. (Most likely, the entire test suite will be rewritten
 * using Spock.)
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Spied {

}
