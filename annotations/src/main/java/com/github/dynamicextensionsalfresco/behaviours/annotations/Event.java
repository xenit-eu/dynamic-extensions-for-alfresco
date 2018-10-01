package com.github.dynamicextensionsalfresco.behaviours.annotations;

/**
 * Indicates the event at an annotation-based Behaviour should be triggered.
 * <p>
 * This enum can be mapped to Alfresco's NotificationFrequency used to register Behaviours.
 * 
 * @author Laurens Fridael
 * 
 */
public enum Event {
	/**
	 * Indicates the Behaviour should be triggered only at the start.
	 */
	FIRST(),

	/**
	 * Indicates the Behaviour should be triggered at every event. In practice, this means the Behaviour is triggered at
	 * the start and at the commit. (I.e. the start and transaction commit are the only events.)
	 */

	ALL(),

	/**
	 * Indicates the Behaviour should be triggered at the end, at the commit of the transaction.
	 */
	COMMIT(),

	/**
	 * Indicates that the value should be inherited or default to {@link #ALL}. This effectively acts as a "null" value
	 * for the {@link ClassPolicy#event()}, {@link AssociationPolicy#event()} and {@link PropertyPolicy#event()}.
	 */
	INHERITED_OR_ALL();

	Event() {
	}

}
