package de.uni_stuttgart.riot.thing;

import java.util.Set;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * A {@link NotificationEvent} is a special type of {@link Event}. When it is fired, all users that have a certain permission on the firing
 * Thing (usually the {@link ThingPermission#READ} permission) will receive a new notification.
 * 
 * @param <E>
 *            The type of the event instances. Note that this can be {@link EventInstance} itself. Subtypes will be scanned for fields
 *            annotated with {@link Parameter}, which will then be available for substitution of variables in title and/or message of the
 *            notification.
 */
public class NotificationEvent<E extends EventInstance> extends Event<E> {

    private final NotificationSeverity severity;
    private final String titleKey;
    private final String messageKey;
    private final Set<ThingPermission> permissions;

    /**
     * Creates a new notification. Note that the constructor is internal. Things are required to instantiate their notifications through
     * {@link Thing#newNotification(String, Class)}.
     * 
     * @param thing
     *            The thing that this event belongs to.
     * @param name
     *            The name of the event.
     * @param severity
     *            The severity of the notifications fired by this event.
     * @param titleKey
     *            The key of the message title in notification.properties.
     * @param messageKey
     *            The key of the message in notification.properties.
     * @param permissions
     *            The permissions that a user needs to have on the thing to receive the notifications fired by this NotificationEvent.
     */
    NotificationEvent(Thing thing, String name, Class<E> instanceType, NotificationSeverity severity, String titleKey, String messageKey, Set<ThingPermission> permissions) {
        super(thing, name, instanceType);
        this.severity = severity;
        this.permissions = permissions;
        this.titleKey = titleKey;
        this.messageKey = messageKey;
    }

    /**
     * Gets the permissions that a user needs to have on the thing to receive the notifications fired by this NotificationEvent.
     * 
     * @return The premissions.
     */
    public Set<ThingPermission> getPermissions() {
        return permissions;
    }

    public NotificationSeverity getSeverity() {
        return severity;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
