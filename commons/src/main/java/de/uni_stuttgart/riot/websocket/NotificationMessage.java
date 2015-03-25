package de.uni_stuttgart.riot.websocket;

import de.uni_stuttgart.riot.notification.Notification;

/**
 * This web socket message simply transports a {@link Notification} that is either new or has changed. If {@link Notification#isDismissed()}
 * is <tt>true</tt>, this means that the notification should be deleted on clients.
 * 
 * @author Philipp Keck
 */
public class NotificationMessage extends WebSocketMessage {

    /**
     * The notification.
     */
    private final Notification notification;

    /**
     * Creates a new instance.
     * 
     * @param notification
     *            The notification.
     */
    public NotificationMessage(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

}
