package com.github.dynamicextensionsalfresco.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.repo.security.authentication.AuthenticationUtil;

/**
 * Indicates methods that are run as the system user.
 * <p>
 * The implementation uses Spring AOP. See the {@link Transactional} documentation for limitations regarding Spring AOP.
 * 
 * @author Laurens Fridael
 * @see AuthenticationUtil#runAsSystem(org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork)
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RunAsSystem {

}
