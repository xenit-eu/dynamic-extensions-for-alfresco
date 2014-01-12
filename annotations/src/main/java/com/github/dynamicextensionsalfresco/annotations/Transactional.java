package com.github.dynamicextensionsalfresco.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.repo.transaction.RetryingTransactionHelper;

/**
 * Indicates methods that are run within a transaction using the given settings. This annotation removes the boilerplate
 * for invoking {@link RetryingTransactionHelper}. <h2>Spring AOP limitations</h2>
 * <p>
 * The underlying implementation relies on Spring AOP and thus the transactional advice is subject to the following
 * limitations:
 * <ul>
 * <li>The annotation can only be applied to <em>public</em> instance methods. The implementation logs a warning if it
 * finds the annotation on a non-public method.
 * <li>Client code must invoke <code>@Transactional</code> methods on Spring-supplied beans directly. Transactional
 * logic should be factored out to public methods of a separate bean.
 * <li>Method calls that are internal to the bean will bypass the transactional advice.
 * </ul>
 * 
 * @author Laurens Fridael
 * @see RetryingTransactionHelper#doInTransaction(org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback,
 *      boolean, boolean)
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

	/**
	 * Specifies if the operation should be run within a read-only transaction.
	 * 
	 * @return
	 */
	boolean readOnly() default false;

	/**
	 * Indicates if the operation requires a new transaction.
	 * 
	 * @return
	 */
	boolean requiresNew() default false;

}
