package de.uni_stuttgart.riot.websocket.server;

import java.util.function.Consumer;

import javax.websocket.Session;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.server.NotificationLogic;
import de.uni_stuttgart.riot.websocket.NotificationMessage;
import de.uni_stuttgart.riot.websocket.WebSocketMessage;

/**
 * The main endpoint, reachable at <tt>/connect/{token}</tt>. This endpoint handles notification registration and will, in the future,
 * support event registrations and actions.
 */
public class WebSocketEndpoint extends ShiroEndpoint {

    /**
     * There path where this endpoint is reachable.
     */
    public static final String PATH = "/connect/{" + TOKEN_PARAM + "}";

    /**
     * The key for the notification listener in the {@link Session#getUserProperties()}.
     */
    private static final String LISTENER_KEY = "user_notifications_listener";

    @Override
    public void onOpen(Session session) {

        // Register a notification listener that will send all new notifications for the logged-in user over the websocket connection.
        Consumer<Notification> listener = new Consumer<Notification>() {
            public void accept(Notification notification) {
                session.getAsyncRemote().sendObject(new NotificationMessage(notification));
            }
        };
        session.getUserProperties().put(LISTENER_KEY, listener);
        NotificationLogic.getNotificationLogic().registerListener(listener);

    }

    @Override
    public void onClose(Session session) {

        // Unregister the listener.
        @SuppressWarnings("unchecked")
        Consumer<Notification> listener = (Consumer<Notification>) session.getUserProperties().get(LISTENER_KEY);
        if (listener != null) {
            session.getUserProperties().remove(LISTENER_KEY);
            NotificationLogic.getNotificationLogic().unregisterListener(listener);
        }

    }

    @Override
    public void onMessage(WebSocketMessage message, Session session) {
        // This endpoint does not expect to receive any web socket messages yet.
        // To handle messages, just check if (message instanceof YourMessage) and then handle it.
    }

}
