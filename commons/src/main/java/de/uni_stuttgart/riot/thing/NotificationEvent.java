package de.uni_stuttgart.riot.thing;

/**
 * A Notification is a special type of {@link Event}. Notifications are presented directly to the user as messages.
 *
 * @param <E>
 *            The type of the event's instances.
 */
public class NotificationEvent<E extends EventInstance> extends Event<E> {

    /**
     * Creates a new notification. Note that the constructor is internal. Things are required to instantiate their notifications through
     * {@link Thing#newNotification(String, Class)}.
     * 
     * @param thing
     *            The thing that this event belongs to.
     * @param name
     *            The name of the event.
     * @param instanceType
     *            The type of instances that will be fired by this event.
     */
    NotificationEvent(Thing thing, String name, Class<E> instanceType) {
        super(thing, name, instanceType);
    }
}
