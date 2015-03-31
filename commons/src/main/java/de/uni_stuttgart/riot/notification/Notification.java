package de.uni_stuttgart.riot.notification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.i18n.NotificationMessages;
import de.uni_stuttgart.riot.thing.BaseInstanceDescriptions;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * A {@link Notification} is a message that needs to be displayed to the user. There are various sources for notifications. For example, a
 * Thing can fire a {@link NotificationEvent}, which will immediately create a new Notification. Another way are rules or other parts of the
 * server that create notifications. Most notifications will refer to a {@link Thing}, but this is not mandatory. Every notification belongs
 * to a user and has a state.<br>
 * <br>
 * The title and message body of a notification are assembled in a special way. Upon generation and delivery of the notification, only the
 * respective keys in <tt>notification_xx.properties</tt> are specified, along with a number of arguments. At the client side, when the
 * notification is to be displayed to the user, the {@link StrSubstitutor} class is used to resolve these Strings to a human-readable
 * format. Use {@link #getMessage()} and {@link #getTitle()} for these tasks. See <tt>notification_xx.properties</tt> for details on the
 * format.
 * 
 * @author Niklas Schnabel
 * @author Philipp Keck
 */
public class Notification extends Storable implements Cloneable {

    /** The ID of the user who the notification is for. */
    private long userID;

    /** The ID of a thing referenced by the notification, may be <tt>null</tt> if the notification is not related to a thing. */
    private Long thingID;

    /** The severity of the notification. */
    private NotificationSeverity severity;

    /** The internal name of the notification type. */
    private String name;

    /** The key of the message title in notification.properties. */
    private String titleKey;

    /** The key of the message in notification.properties. */
    private String messageKey;

    /** Arguments for the title and/or message */
    private Map<String, Object> arguments = new HashMap<String, Object>();

    /** The time the notification was created. */
    private Date time;

    /** True if the user dismissed the notification. */
    private boolean dismissed;

    @JsonIgnore
    private transient String resolvedTitle;

    @JsonIgnore
    private transient String resolvedMessage;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public Long getThingID() {
        return thingID;
    }

    public void setThingID(Long thingID) {
        this.thingID = thingID;
    }

    public NotificationSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(NotificationSeverity severity) {
        this.severity = severity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * Sets the arguments map.
     * 
     * @param arguments
     *            The new arguments.
     */
    public void setArguments(Map<String, Object> arguments) {
        if (arguments == null) {
            this.arguments = new HashMap<String, Object>();
        } else {
            this.arguments = arguments;
        }
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    /**
     * Resolves the title of the notification and substitutes arguments.
     * 
     * @return The title that can be displayed to the user.
     */
    @JsonIgnore
    public String getTitle() {
        if (resolvedTitle == null) {
            resolveStrings();
        }
        return resolvedTitle;
    }

    public void setResolvedTitle(String resolvedTitle) {
        this.resolvedTitle = resolvedTitle;
    }

    /**
     * Resolves the message of the notification and substitutes arguments.
     * 
     * @return The message that can be displayed to the user.
     */
    @JsonIgnore
    public String getMessage() {
        if (resolvedMessage == null) {
            resolveStrings();
        }
        return resolvedMessage;
    }

    public void setResolvedMessage(String resolvedMessage) {
        this.resolvedMessage = resolvedMessage;
    }

    private void resolveStrings() {
        StrSubstitutor substitutor = new StrSubstitutor(arguments, "{", "}");
        resolvedTitle = substitutor.replace(NotificationMessages.INSTANCE.getString(titleKey));
        resolvedMessage = substitutor.replace(NotificationMessages.INSTANCE.getString(messageKey));
    }

    @Override
    public Notification clone() {
        try {
            return (Notification) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a notification with an empty user ID from the given notification event instance. Note that this will call
     * {@link Object#toString()} on all argument values.
     * 
     * @param notificationEvent
     *            The notification event.
     * @param eventInstance
     *            The event instance.
     * @return A notification with the values from the event and event instance. This will not have {@link #userID} set!
     */
    public static Notification create(NotificationEvent<?> notificationEvent, EventInstance eventInstance) {
        Notification notification = new Notification();
        notification.name = eventInstance.getName();
        notification.severity = notificationEvent.getSeverity();
        notification.titleKey = notificationEvent.getTitleKey();
        notification.messageKey = notificationEvent.getMessageKey();
        notification.time = eventInstance.getTime();
        notification.thingID = notificationEvent.getThing().getId();
        notification.arguments.put("thing.name", notificationEvent.getThing().getName());
        if (notification.thingID != eventInstance.getThingId()) {
            throw new IllegalArgumentException("notificationEvent and eventInstance belong to different Things!");
        }

        for (Map.Entry<String, Object> parameter : BaseInstanceDescriptions.getParameterValues(eventInstance).entrySet()) {
            Object value = parameter.getValue();
            if (value instanceof Thing) {
                value = ((Thing) value).getName();
            }
            notification.arguments.put(parameter.getKey(), value);
        }
        return notification;
    }

}
