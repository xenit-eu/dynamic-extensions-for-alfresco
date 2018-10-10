package com.github.dynamicextensionsalfresco.util;

import com.github.dynamicextensionsalfresco.behaviours.annotations.Event;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;

/**
 * Util class that can be used to convert a Dynamic Extensions {@link Event} to Alfresco's {@link
 * NotificationFrequency}
 */
public class EventUtil {

    public static NotificationFrequency toNotificationFrequency(final Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Cannot convert null to a NotificationFrequency");
        }
        switch (event) {
            case FIRST:
                return NotificationFrequency.FIRST_EVENT;
            case ALL:
                return NotificationFrequency.EVERY_EVENT;
            case COMMIT:
                return NotificationFrequency.TRANSACTION_COMMIT;
            case INHERITED_OR_ALL:
                return NotificationFrequency.EVERY_EVENT;
            default:
                final String message = "Unknown NotificationFrequency: '" + event.name() + "'";
                throw new IllegalArgumentException(message);
        }
    }

}
