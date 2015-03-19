package de.uni_stuttgart.riot.thing.server;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.AuthenticatingThingBehavior;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;

/**
 * On the server, a thing behaves as follows:
 * <ul>
 * <li>It collects the actions that are called upon the thing to be fetched regularly by the client that executes the thing.</li>
 * <li>It collects the events of other things that it registered to to be fetched regularly.</li>
 * <li>It mirrors the property values of the actual thing and updates them as change events come in.</li>
 * </ul>
 * 
 * @author Philipp Keck
 */
public class ServerThingBehavior extends ThingBehavior implements AuthenticatingThingBehavior {

    private final Queue<ActionInstance> outstandingActions = new LinkedList<>();
    private final Queue<EventInstance> occuredEvents = new LinkedList<>();
    private final Map<Long, ThingShare> shares = new HashMap<>();
    private Date lastConnection;

    /**
     * Listens to events of other things and pushes all of them to {@link #occuredEvents}.
     */
    private final EventListener<EventInstance> eventListener = new EventListener<EventInstance>() {
        public void onFired(Event<? extends EventInstance> event, EventInstance eventInstance) {
            occuredEvents.add(eventInstance);
        }
    };

    /**
     * Creates a new instance and registers it with the {@link ServerReferenceResolver}.
     */
    public ServerThingBehavior() {
        super(ServerReferenceResolver.getInstance());
    }

    @Override
    protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        // Transport the action to the executing thing.
        getActionFromInstance(actionInstance); // Just check if it's there.
        outstandingActions.add(actionInstance);
    }

    /**
     * Adds or updates the permission for the user. If the permissions are empty, all permissions will be revoked. Existing permissions will
     * be replaced.
     * 
     * @param share
     *            The thing share containing the user and his/her permissions.
     */
    public void addOrUpdateShare(ThingShare share) {
        if (share == null) {
            throw new IllegalArgumentException("share must not be null!");
        } else if (share.isEmpty()) {
            shares.remove(share.getUserId());
        } else if (share.getUserId() < 1) {
            throw new IllegalArgumentException("userID must be set!");
        } else {
            shares.put(share.getUserId(), share);
        }
    }

    /**
     * Removes all permissions for the given user. If it does not exist, it will be ignored.
     * 
     * @param userId
     *            The user id.
     */
    public void removeShare(long userId) {
        shares.remove(userId);
    }

    @Override
    public boolean canAccess(Long userId, ThingPermission permission) {
        ThingShare share = shares.get(userId);
        if (share != null && share.permits(permission)) {
            return true;
        } else if (getThing().hasParent()) {
            try {
                return ThingLogic.getThingLogic().canAccess(getThing().getParentId(), userId, permission);
            } catch (DatasourceFindException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets all current shares of this thing.
     * 
     * @return The permissions.
     */
    public Collection<ThingShare> getShares() {
        return shares.values();
    }

    /**
     * The time at which the thing connected to the server for the last time.
     * 
     * @return The last connection time.
     */
    public Date getLastConnection() {
        return lastConnection;
    }

    /**
     * Sets the value of {@link #lastConnection} to now.
     */
    public void markLastConnection() {
        lastConnection = new Date();
    }

    /**
     * Registers to the event and in the future reports on all event instances of the event.
     * 
     * @param event
     *            The event to register to.
     */
    public void registerToEvent(Event<?> event) {
        event.register(eventListener);
    }

    /**
     * Unregisters from the event and stops reporting on the event's instances in the future.
     * 
     * @param event
     *            The event to unregister from.
     */
    public void unregisterFromEvent(Event<?> event) {
        event.unregister(eventListener);
    }

    /**
     * Fires the given event locally (on the server), which will cause listeners to transport it to the clients. Note that for
     * {@link PropertyChangeEvent}s this will also change the property's value.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param eventInstance
     *            The event instance.
     */
    <E extends EventInstance> void fireEvent(E eventInstance) {
        notifyListeners(getEventFromInstance(eventInstance), eventInstance);
    }

    /**
     * Retrieves the updates since the last call of this method.
     * 
     * @return The updates.
     */
    public ThingUpdatesResponse getUpdates() {
        ThingUpdatesResponse response = new ThingUpdatesResponse();
        response.setOccuredEvents(moveQueue(occuredEvents));
        response.setOutstandingActions(moveQueue(outstandingActions));
        markLastConnection();
        return response;
    }

    /**
     * Thread-safe implementation to move the content of a queue to a new collection. Ensures that no element goes missing.
     * 
     * @param queue
     *            The queue to read from. It will be empty in the end.
     * @return A new collection containing all elements of the queue.
     */
    private static <I> Collection<I> moveQueue(Queue<I> queue) {
        Collection<I> result = new ArrayList<I>(queue.size());
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }
        return result;
    }

}
