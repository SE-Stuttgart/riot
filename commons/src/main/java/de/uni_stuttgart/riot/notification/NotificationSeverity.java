package de.uni_stuttgart.riot.notification;

/**
 * Represents the state of a notification.
 * 
 * @author Philipp Keck
 */
public enum NotificationSeverity {

    /**
     * The notification contains some information for the user that he could read at any time -- just to be informed.
     */
    INFO,

    /**
     * The notification contains some information that is closely related to the current time, e.g., an upcoming calendar event, etc. The
     * information might not be useful to the user anymore after a while.
     */
    INFO_NOW,

    /**
     * The notification contains information about maintenance tasks related the system or the user's Things. These are special kinds of
     * {@link #INFO} messages, but definetely not urgent.
     */
    MAINTENANCE,

    /**
     * The notification contains a warning for the user, that is, one of his Things or the system might stop working if he does not react.
     */
    WARNING,

    /**
     * The notification contains an important warning ({@see #WARNING}).
     */
    WARNING_IMPORTANT,

    /**
     * The notification contains an error message from the system or one of the user's Things, that is, the thing stopped working.
     */
    ERROR

}
