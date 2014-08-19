package com.github.dynamicextensionsalfresco.behaviours.annotations;

import org.alfresco.repo.policy.Behaviour.NotificationFrequency;

/**
 * Indicates the event at an annotation-based Behaviour should be triggered.
 * <p>
 * This enum maps directly to the {@link NotificationFrequency} used to register Behaviours.
 * 
 * @author Laurens Fridael
 * 
 */
public enum Event {
	/**
	 * Indicates the Behaviour should be triggered only at the start.
	 * 
	 * @see NotificationFrequency#FIRST_EVENT
	 */
	FIRST(NotificationFrequency.FIRST_EVENT),

	/**
	 * Indicates the Behaviour should be triggered at every event. In practice, this means the Behaviour is triggered at
	 * the start and at the commit. (I.e. the start and transaction commit are the only events.)
	 * 
	 * @see NotificationFrequency#EVERY_EVENT
	 */

	ALL(NotificationFrequency.EVERY_EVENT),

	/**
	 * Indicates the Behaviour should be triggered at the end, at the commit of the transaction.
	 * 
	 * @see NotificationFrequency#TRANSACTION_COMMIT
	 */
	COMMIT(NotificationFrequency.TRANSACTION_COMMIT),

	/**
	 * Indicates that the value should be inherited or default to {@link #ALL}. This effectively acts as a "null" value
	 * for the {@link ClassPolicy#event()}, {@link AssociationPolicy#event()} and {@link PropertyPolicy#event()}.
	 */
	INHERITED_OR_ALL(NotificationFrequency.EVERY_EVENT);

	private final NotificationFrequency notificationFrequency;

	private Event(final NotificationFrequency notificationFrequency) {
		this.notificationFrequency = notificationFrequency;
	}

	public NotificationFrequency toNotificationFrequency() {
		return notificationFrequency;
	}

}
