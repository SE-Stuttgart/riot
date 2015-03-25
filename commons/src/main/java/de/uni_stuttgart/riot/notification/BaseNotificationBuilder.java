package de.uni_stuttgart.riot.notification;

import java.util.Date;

import de.uni_stuttgart.riot.thing.Thing;

/**
 * A notification builder allows to build {@link Notification}s with the Builder pattern and then do something with them (which is defined
 * by the actual builder implementation).
 * 
 * @author Philipp Keck
 * @param <B>
 *            The actual builder type. Subclasses should replace this with their own class name.
 */
public abstract class BaseNotificationBuilder<B extends BaseNotificationBuilder<B>> {

    protected final Notification notification;

    /**
     * Creates a new builder.
     */
    public BaseNotificationBuilder() {
        this(new Notification());
    }

    /**
     * Creates a new builder.
     * 
     * @param notification
     *            A notification object containing the initial values.
     */
    public BaseNotificationBuilder(Notification notification) {
        this.notification = notification;
    }

    /**
     * Returns <tt>this</tt> builder instance, typed as <tt>B</tt> for further use.
     * 
     * @return The builder itself.
     */
    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    /**
     * Sets {@link Notification#setUserID(long)}.
     * 
     * @param userID
     *            The ID of the user who the notification is for.
     * @return The builder.
     */
    public B forUser(long userID) {
        notification.setUserID(userID);
        return self();
    }

    /**
     * Sets {@link Notification#setName(String)}.
     * 
     * @param name
     *            The name of the notification type.
     * @return The builder.
     */
    public B name(String name) {
        notification.setName(name);
        return self();
    }

    /**
     * Sets {@link Notification#setTitleKey(String)}.
     * 
     * @param titleKey
     *            The titleKey of the notification.
     * @return The builder.
     */
    public B titleKey(String titleKey) {
        notification.setTitleKey(titleKey);
        return self();
    }

    /**
     * Sets {@link Notification#setMessageKey(String)}.
     * 
     * @param messageKey
     *            The messageKey of the notification.
     * @return The builder.
     */
    public B messageKey(String messageKey) {
        notification.setMessageKey(messageKey);
        return self();
    }

    /**
     * Sets {@link Notification#setThingID(Long)}.
     * 
     * @param thing
     *            The thing that the notification refers to.
     * @return The builder.
     */
    public B forThing(Thing thing) {
        if (thing == null) {
            notification.setThingID(null);
            return self();
        } else {
            notification.setThingID(thing.getId());
            return param("thing.name", thing.getName());
        }
    }

    /**
     * Sets {@link Notification#setSeverity(NotificationSeverity)}.
     * 
     * @param severity
     *            The severity of the notification.
     * @return The builder.
     */
    public B severity(NotificationSeverity severity) {
        notification.setSeverity(severity);
        return self();
    }

    /**
     * Alias for {@link #argument(String, Object)}.
     * 
     * @param argumentName
     *            The name of the argument.
     * @param argumentValue
     *            The value of the argument.
     * @return The builder.
     */
    public B param(String argumentName, Object argumentValue) {
        return argument(argumentName, argumentValue);
    }

    /**
     * Adds an entry to {@link Notification#getArguments()}. Existing entries will be overwritten.
     * 
     * @param argumentName
     *            The name of the argument.
     * @param argumentValue
     *            The value of the argument.
     * @return The builder.
     */
    public B argument(String argumentName, Object argumentValue) {
        notification.getArguments().put(argumentName, argumentValue);
        return self();
    }

    /**
     * Sets the {@link Notification#getTime()} to now and then returns the built notification. This method throws
     * {@link IllegalStateException} if there are values missing.
     * 
     * @return The notification.
     */
    protected Notification build() {
        notification.setTime(new Date());
        return notification;
    }

}
