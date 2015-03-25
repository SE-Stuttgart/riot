package de.uni_stuttgart.riot.notification;

/**
 * A notification builder allows to build {@link Notification}s with the Builder pattern.
 * 
 * @author Philipp Keck
 */
public final class NotificationBuilder extends BaseNotificationBuilder<NotificationBuilder> {

    @Override
    public Notification build() {
        return super.build();
    }

    /**
     * Creates a new builder.
     * 
     * @return The new builder.
     */
    public static NotificationBuilder create() {
        return new NotificationBuilder();
    }

}
