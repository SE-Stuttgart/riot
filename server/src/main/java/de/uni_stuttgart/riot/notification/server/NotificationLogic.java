package de.uni_stuttgart.riot.notification.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.shiro.authz.UnauthorizedException;

import de.uni_stuttgart.riot.db.NotificationDAO;
import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * This is the main interface for {@link Notification} handling on the server.
 */
public class NotificationLogic {

    /**
     * Instance of the singleton pattern.
     */
    private static NotificationLogic instance;

    /**
     * The Notification DAO.
     */
    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * The User Management Facade.
     */
    private final UserManagementFacade umFacade = UserManagementFacade.getInstance();

    /**
     * Notification listeners ordered by user IDs.
     */
    private final Map<Long, List<Consumer<Notification>>> listeners = new HashMap<Long, List<Consumer<Notification>>>();

    /**
     * Getter for {@link NotificationLogic}.
     * 
     * @return Instance of {@link NotificationLogic}
     */
    public static NotificationLogic getNotificationLogic() {
        if (instance == null) {
            instance = new NotificationLogic();
        }
        return instance;
    }

    /**
     * Gets all notifications for the given user that have not yet been dismissed.
     * 
     * @param userID
     *            The ID of the user. If this parameter is <tt>null</tt>, it will be replaced by the ID of the current user.
     * @throws DatasourceFindException
     *             When retrieving the data failed.
     * @return The notifications.
     */
    public Collection<Notification> getOutstandingNotifications(Long userID) throws DatasourceFindException {
        return notificationDAO.findOutstandingNotifications(userID == null ? umFacade.getCurrentUserId() : userID);
    }

    /**
     * Returns a paginated collection of the user's notification (also dismissed ones).
     * 
     * @param userID
     *            The ID of the user. If this parameter is <tt>null</tt>, it will be replaced by the ID of the current user.
     * @param offset
     *            The offset of the first entry to return.
     * @param limit
     *            The maximum number of entries to return.
     * @return The notifications.
     * @throws DatasourceFindException
     *             When retrieving the data failed.
     */
    public Collection<Notification> findNotifications(Long userID, int offset, int limit) throws DatasourceFindException {
        return notificationDAO.findAll(userID == null ? umFacade.getCurrentUserId() : userID, offset, limit);
    }

    /**
     * Checks that the given notification belongs to the given/current user and then writes all fields to the database that may be changed.
     * Unchangeable fields will be ignored.
     * 
     * @param notification
     *            The notification.
     * @param checkUserId
     *            The expected user ID. If this parameter is <tt>null</tt>, it will be replaced by the current user ID.
     * @throws DatasourceFindException
     *             When the notification to update does not exist.
     * @throws DatasourceUpdateException
     *             When writing to the database fails.
     */
    public void updateNotification(Notification notification, Long checkUserId) throws DatasourceFindException, DatasourceUpdateException {
        if (notification.getId() == null || notification.getId() < 1) {
            throw new IllegalArgumentException("notification must have its ID set!");
        }

        Notification original = notificationDAO.findBy(notification.getId());
        if (original.getUserID() != (checkUserId == null ? umFacade.getCurrentUserId() : checkUserId)) {
            throw new UnauthorizedException("Cannot access notification " + notification.getId());
        }
        notificationDAO.updateDismissed(original.getId(), original.getUserID(), notification.isDismissed());
        original.setDismissed(notification.isDismissed());
        notifyListeners(original);
    }

    /**
     * Fires the given notification event by creating {@link Notification}s for all affected users, storing them in the database and sending
     * them out to clients.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param notificationEvent
     *            The notification event.
     * @param eventInstance
     *            The event instance.
     * @throws DatasourceFindException
     *             When the Thing that the event belongs to does not exist.
     */
    public <E extends EventInstance> void fireNotificationEvent(NotificationEvent<E> notificationEvent, E eventInstance) throws DatasourceFindException {
        Notification notification = Notification.create(notificationEvent, eventInstance);
        postNotificationToPermittedUsers(notification, notificationEvent.getPermissions());
    }

    /**
     * Sends the given notification by creating clones for all affected users, storing them in the database and sending them out to clients.
     * 
     * @param notification
     *            The notification to be sent. Its Thing ID must be set!
     * @param permissions
     *            The permissions that recipients need to have on the thing.
     * @throws DatasourceFindException
     *             When the Thing referenced by the notification does not exist.
     */
    public void postNotificationToPermittedUsers(final Notification notification, Set<ThingPermission> permissions) throws DatasourceFindException {
        if (notification.getThingID() < 1) {
            throw new IllegalArgumentException("thingID must not be empty!");
        }

        Set<Long> userIDs = ThingLogic.getThingLogic().getPermittedUsers(notification.getThingID(), permissions);
        for (long userID : userIDs) {
            Notification cloned = notification.clone();
            cloned.setUserID(userID);
            postNotification(cloned);
        }
    }

    /**
     * Saves a new notification in the database and sends it out to connected devices of the affected user.
     * 
     * @param notification
     *            The noficiation to be posted.
     */
    public void postNotification(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("notification must not be null");
        } else if (notification.getUserID() < 1) {
            throw new IllegalArgumentException("userID must be set!");
        }

        try {
            notificationDAO.create(notification);
        } catch (DatasourceInsertException e) {
            throw new RuntimeException(e);
        }

        notifyListeners(notification);
    }

    /**
     * Notifies all listeners that registered for notifications of the respective user.
     * 
     * @param notification
     *            The notification to call the listeners for.
     */
    private synchronized void notifyListeners(Notification notification) {
        List<Consumer<Notification>> userListeners = listeners.get(notification.getUserID());
        if (userListeners != null) {
            for (Consumer<Notification> listener : userListeners) {
                listener.accept(notification);
            }
        }
    }

    /**
     * Registers a listener to receive all new notifications for the current user. See {@link #registerListener(long, Consumer)}.
     * 
     * @param listener
     *            The listener.
     */
    public synchronized void registerListener(Consumer<Notification> listener) {
        registerListener(umFacade.getCurrentUserId(), listener);
    }

    /**
     * Registers a listener to receive all new notifications for a user. Use the {@link NotificationLogic} to retrieve the current ones.
     * Note that changed notifications (particularly when their {@link Notification#isDismissed()} status changed) will be sent to the
     * listener again.
     * 
     * @param userID
     *            The user ID.
     * @param listener
     *            The listener.
     */
    public synchronized void registerListener(long userID, Consumer<Notification> listener) {
        if (userID < 1) {
            throw new IllegalArgumentException("Invalid user ID " + userID);
        }
        List<Consumer<Notification>> userListeners = listeners.get(userID);
        if (userListeners == null) {
            userListeners = new ArrayList<>();
            listeners.put(userID, userListeners);
        }
        userListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * Unregisters the given listener for the current user. See {@link #unregisterListener(long, Consumer)}.
     * 
     * @param listener
     *            The listener.
     */
    public synchronized void unregisterListener(Consumer<Notification> listener) {
        unregisterListener(umFacade.getCurrentUserId(), listener);
    }

    /**
     * Unregisters the given listener. See {@link #registerListener(long, Consumer)}.
     * 
     * @param userID
     *            The user ID.
     * @param listener
     *            The listener.
     */
    public synchronized void unregisterListener(long userID, Consumer<Notification> listener) {
        if (userID < 1) {
            throw new IllegalArgumentException("Invalid user ID " + userID);
        }
        List<Consumer<Notification>> userListeners = listeners.get(userID);
        if (userListeners != null) {
            userListeners.remove(listener);
            if (userListeners.isEmpty()) {
                listeners.remove(userID);
            }
        }
    }

}
