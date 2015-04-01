package de.uni_stuttgart.riot.websocket.server;

import java.util.function.Consumer;

import javax.websocket.Session;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.server.NotificationLogic;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.websocket.NotificationMessage;
import de.uni_stuttgart.riot.websocket.PropertyChangeMessage;
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
    private static final String NOTIFICATION_LISTENER_KEY = "user_notifications_listener";

    /**
     * The key for the thing event listener in the {@link Session#getUserProperties()}.
     */
    private static final String EVENT_LISTENER_KEY = "user_event_listener";

    @Override
    public void onOpen(Session session) {

        // Register a notification listener that will send all new notifications for the logged-in user over the websocket connection.
        Consumer<Notification> notificationListener = new Consumer<Notification>() {
            public void accept(Notification notification) {
                session.getAsyncRemote().sendObject(new NotificationMessage(notification));
            }
        };
        session.getUserProperties().put(NOTIFICATION_LISTENER_KEY, notificationListener);
        NotificationLogic.getNotificationLogic().registerListener(notificationListener);

        // Register an event listener that will send all occurred events for all things the current logged-in user has read access to over
        // the websocket connection.
        EventListener<EventInstance> eventListener = new EventListener<EventInstance>() {
            @Override
            public void onFired(Event<? extends EventInstance> event, EventInstance eventInstance) {
                if (event instanceof PropertyChangeEvent<?>) {
                    session.getAsyncRemote().sendObject(new PropertyChangeMessage(eventInstance));
                }
            }
        };
        session.getUserProperties().put(EVENT_LISTENER_KEY, eventListener);
        ThingLogic.getThingLogic().registerToAllEvents(null, eventListener);
    }

    @Override
    public void onClose(Session session) {

        // Unregister the notification listener.
        @SuppressWarnings("unchecked")
        Consumer<Notification> notificationListener = (Consumer<Notification>) session.getUserProperties().get(NOTIFICATION_LISTENER_KEY);
        if (notificationListener != null) {
            session.getUserProperties().remove(NOTIFICATION_LISTENER_KEY);
            NotificationLogic.getNotificationLogic().unregisterListener(notificationListener);
        }

        // Unregister the thing event listener.
        @SuppressWarnings("unchecked")
        EventListener<EventInstance> eventListener = (EventListener<EventInstance>) session.getUserProperties().get(EVENT_LISTENER_KEY);
        if (eventListener != null) {
            session.getUserProperties().remove(EVENT_LISTENER_KEY);
            ThingLogic.getThingLogic().unregisterFromAllEvents(null, eventListener);
        }
    }

    @Override
    public void onMessage(WebSocketMessage message, Session session) {
        // This endpoint does not expect to receive any web socket messages yet.
        // To handle messages, just check if (message instanceof YourMessage) and then handle it.
    }

}
