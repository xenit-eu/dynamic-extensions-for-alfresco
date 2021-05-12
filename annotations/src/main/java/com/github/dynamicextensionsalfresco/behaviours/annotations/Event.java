package com.github.dynamicextensionsalfresco.behaviours.annotations;

/**
 * Indicates the event at an annotation-based Behaviour should be triggered.
 * <p>
 * This enum maps directly to the {@link org.alfresco.repo.policy.Behaviour.NotificationFrequency} used to register
 * Behaviours.
 *
 * @author Laurens Fridael
 */
public enum Event {
    /**
     * Indicates the Behaviour should be triggered only at the start.
     *
     * @see org.alfresco.repo.policy.Behaviour.NotificationFrequency#FIRST_EVENT
     */
    FIRST(),

    /**
     * Maps to Alfresco's EVERY_EVENT NotificationFrequency. From the official Alfresco documentation: "The event
     * handler is then just executed wherever it is being invoked in the code. The name of this notification frequency
     * implies that the event handler will be called multiple times, which is true. The EVERY_EVENT notification
     * frequency can be called numerous times within a single transaction (a lot more than you might expect), so unless
     * your behavior logic is fast+in-repository-only (no RPCs, no external data access, etc.) or asynchronous, it’s
     * easy to seriously impact Alfresco’s performance. ..."
     *
     * @see org.alfresco.repo.policy.Behaviour.NotificationFrequency#EVERY_EVENT
     */
    ALL(),

    /**
     * Indicates the Behaviour should be triggered at the end, at the commit of the transaction.
     *
     * @see org.alfresco.repo.policy.Behaviour.NotificationFrequency#TRANSACTION_COMMIT
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
