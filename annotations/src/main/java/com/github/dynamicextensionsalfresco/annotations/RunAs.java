package com.github.dynamicextensionsalfresco.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates methods that are run as a given user.
 * <p>
 * The implementation uses Spring AOP. See the {@link Transactional} documentation for limitations regarding Spring
 * AOP.
 *
 * @author Laurens Fridael
 * @see org.alfresco.repo.security.authentication.AuthenticationUtil#runAs(org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork,
 * String)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RunAs {

    /**
     * The user to run this method as.
     */
    String value();
}
