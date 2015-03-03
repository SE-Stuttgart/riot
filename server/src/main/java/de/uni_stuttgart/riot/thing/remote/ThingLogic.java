package de.uni_stuttgart.riot.thing.remote;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.db.thing.ThingDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * This is the main interface for {@link Thing} handling on the server.
 */
public class ThingLogic {

    /**
     * Instance of the singleton pattern.
     */
    private static ThingLogic instance;

    /**
     * Contains all known things. These need to be held in memory to allow for links like event listeners, etc. It is important to use a Map
     * implementation that ensures a consistent order on the entries because paginated requests are done directly on this Map.
     */
    private final Map<Long, ServerThingBehavior> things = new TreeMap<>();

    /**
     * This behavior factory will create thing behaviors for the server (that will work along with this ThingLogic implementation) and it
     * will add every new thing to {@link #things}.
     */
    private final ThingBehaviorFactory<ServerThingBehavior> behaviorFactory = new ThingBehaviorFactory<ServerThingBehavior>() {
        public ServerThingBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
            return new ServerThingBehavior();
        }

        public void onThingCreated(Thing thing, ServerThingBehavior behavior) {
            if (thing.getId() != null && thing.getId() != 0) {
                things.put(thing.getId(), behavior);
            }
        }
    };

    /**
     * The Thing DAO.
     */
    private final ThingDAO thingDAO = new ThingDAO(behaviorFactory);

    /**
     * Creates a new ThingLogic instance.
     * 
     * @throws DatasourceFindException
     *             If the things could not be loaded.
     */
    private ThingLogic() throws DatasourceFindException {
        this.initStoredThings();
    }

    /**
     * Getter for {@link ThingLogic}.
     * 
     * @return Instance of {@link ThingLogic}
     * @throws DatasourceFindException
     *             Exception on initialization of Thing due to datasource exception.
     */
    public static ThingLogic getThingLogic() {
        if (instance == null) {
            try {
                instance = new ThingLogic();
            } catch (DatasourceFindException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * Reads all stored things from the database.
     * 
     * @throws DatasourceFindException
     */
    private void initStoredThings() throws DatasourceFindException {
        // Note that because of the behaviorFactory above, this will fill the things List.
        thingDAO.findAll();
    }

    /**
     * Returns a Thing by its ID.
     * 
     * @param id
     *            The ID of the thing.
     * @return The thing or <tt>null</tt> if it could not be found.
     */
    public Thing getThing(long id) {
        ThingBehavior behavior = things.get(id);
        return behavior == null ? null : behavior.getThing();
    }

    /**
     * Retrieves the behavior of a thing. Fails if it does not exist.
     * 
     * @param id
     *            The ID of the thing.
     * @return The thing's behavior.
     * @throws DatasourceFindException
     *             If the thing could not be found.
     */
    protected ServerThingBehavior getBehavior(long id) throws DatasourceFindException {
        ServerThingBehavior behavior = things.get(id);
        if (behavior == null) {
            throw new DatasourceFindException("A thing with the ID " + id + " does not exist");
        }
        return behavior;
    }

    /**
     * Returns a filtered collection of things.
     * 
     * @param filter
     *            The filter.
     * @return The things that match the filter.
     */
    public Collection<Thing> findThings(FilteredRequest filter) {
        Stream<Thing> stream = things.values().stream().map(ThingBehavior::getThing);

        if (!filter.getFilterAttributes().isEmpty()) {
            stream = stream.filter(thing -> {
                Predicate<? super FilterAttribute> predicate = (attribute) -> {
                    switch (attribute.getFieldName()) {
                    case "id":
                    case "thingID":
                        return attribute.evaluate(thing.getId());
                    case "name":
                        return attribute.evaluate(thing.getName());
                    case "type":
                        return attribute.evaluate(thing.getClass().getName()) || attribute.evaluate(thing.getClass().getSimpleName());
                    default:
                        return false;
                    }
                };

                if (filter.isOrMode()) {
                    return filter.getFilterAttributes().stream().anyMatch(predicate);
                } else {
                    return filter.getFilterAttributes().stream().allMatch(predicate);
                }
            });

        }

        stream = stream.skip(filter.getOffset());
        if (filter.getLimit() > 0) {
            stream = stream.limit(filter.getLimit());
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Returns a paginated collection of things.
     * 
     * @param offset
     *            The offset (first index to be returned).
     * @param limit
     *            The number of things to be returned.
     * @return The matching collection of things.
     */
    public Collection<Thing> findThings(int offset, int limit) {
        Stream<ServerThingBehavior> stream = things.values().stream().skip(offset);
        if (limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.map(ThingBehavior::getThing).collect(Collectors.toList());
    }

    /**
     * Returns a collection of all known things.
     * 
     * @return All things.
     */
    public Collection<Thing> getAllThings() {
        return things.values().stream().map(ThingBehavior::getThing).collect(Collectors.toList());
    }

    /**
     * Registers a thing in the thing logic. Includes storing the thing in the datasource. This operation must be called once for every
     * thing to be used in any way regarding server operations.
     * 
     * @param thingType
     *            The type of the thing to be registered.
     * @param thingName
     *            The name of the thing to be registerd.
     * @param ownerId
     *            The User ID of the user who owns the thing.
     * @return The newly registered Thing.
     * @throws DatasourceInsertException
     *             Exception during creation of the thing or during insertion of the thing into the datasource
     */
    public synchronized Thing registerThing(String thingType, String thingName, long ownerId) throws DatasourceInsertException {
        try {
            // Note that the thing will be added to the things list by the behaviorFactory.
            Thing thing = ThingFactory.create(thingType, 0, thingName, behaviorFactory);
            ServerThingBehavior behavior = (ServerThingBehavior) thing.getBehavior();
            thing.setOwnerId(ownerId);
            thingDAO.insert(thing);
            things.put(thing.getId(), behavior);
            behavior.markLastConnection();
            return thing;
        } catch (Exception e) {
            throw new DatasourceInsertException(e);
        }
    }

    /**
     * Unregisters a thing. Includes removal of the thing from the datasource.
     * 
     * @param id
     *            thing id of the thing to be unregistered.
     * @throws DatasourceDeleteException
     *             Exception during removal form datasource, e.g. no thing with given id exists in the datasource
     */
    public synchronized void unregisterThing(long id) throws DatasourceDeleteException {
        ServerThingBehavior behavior = things.get(id);
        if (behavior == null) {
            throw new DatasourceDeleteException("Thing with id " + id + " does not exist!");
        }
        thingDAO.delete(id);
        things.remove(id);
    }

    /**
     * Registers the given thing (<tt>observerThingID</tt>) on the event <tt>targetEventName</tt> of thing with id <tt>targetThingID</tt>.
     * 
     * @param observerThingID
     *            The Thing that wants to register
     * @param targetThingID
     *            The Thing that is being observed, i.e., the thing that owns the event.
     * @param targetEventName
     *            The name of the event to register to.
     * @throws DatasourceFindException
     *             If one of the things cannot be found or if the event does not exist.
     */
    public synchronized void registerToEvent(long observerThingID, final long targetThingID, String targetEventName) throws DatasourceFindException {
        ServerThingBehavior observerBehavior = getBehavior(observerThingID);
        ServerThingBehavior targetBehavior = getBehavior(targetThingID);
        Event<?> event = targetBehavior.getThing().getEvent(targetEventName);
        if (event == null) {
            throw new DatasourceFindException("The thing with ID " + targetThingID + " of type " + targetBehavior.getThing().getClass() + " does not have the event " + targetEventName);
        }

        observerBehavior.registerToEvent(event);
    }

    /**
     * Unregisters the given thing (<tt>observerThingID</tt>) from the event <tt>targetEventName</tt> of thing with id
     * <tt>targetThingID</tt>.
     * 
     * @param observerThingID
     *            The Thing that wants to unregister
     * @param targetThingID
     *            The Thing that is being observed, i.e., the thing that owns the event.
     * @param targetEventName
     *            The name of the event to unregister from.
     * @throws DatasourceFindException
     *             If one of the things cannot be found or if the event does not exist.
     */
    public synchronized void unregisterFromEvent(long observerThingID, final long targetThingID, String targetEventName) throws DatasourceFindException {
        ServerThingBehavior observerBehavior = getBehavior(observerThingID);
        ServerThingBehavior targetBehavior = getBehavior(targetThingID);
        Event<?> event = targetBehavior.getThing().getEvent(targetEventName);
        if (event == null) {
            throw new DatasourceFindException("The thing with ID " + targetThingID + " of type " + targetBehavior.getThing().getClass() + " does not have the event " + targetEventName);
        }

        observerBehavior.unregisterFromEvent(event);
    }

    /**
     * Returns the last time the given thing has called the server. Returns 1.1.1970 if the thing has never called the server.
     * 
     * @param id
     *            thing id
     * @return last server call time
     * @throws DatasourceFindException
     *             If the thing was not found.
     */
    public Date getLastConnection(long id) throws DatasourceFindException {
        return getBehavior(id).getLastConnection();
    }

    /**
     * Fires an {@link EventInstance} on the server so that every thing that was registered on it will receive the instance.
     * 
     * @param eventInstance
     *            {@link EventInstance} to be fired.
     * @throws DatasourceFindException
     *             If there is no thing with {@link EventInstance#getThingId()} registered
     */
    public void fireEvent(EventInstance eventInstance) throws DatasourceFindException {
        getBehavior(eventInstance.getThingId()).fireEvent(eventInstance);
    }

    /**
     * Submits the given {@link ActionInstance} to the thing with id {@link ActionInstance#getThingId()}. So that the result of calling
     * {@link ThingLogic#getThingUpdates(long)} with {@link ActionInstance#getThingId()} would include the given a actionInstance.
     * 
     * @param actionInstance
     *            {@link ActionInstance} that should be submitted
     * @throws DatasourceFindException
     *             if there is no thing with {@link ActionInstance#getThingId()} registered
     */
    public synchronized void submitAction(ActionInstance actionInstance) throws DatasourceFindException {
        getBehavior(actionInstance.getThingId()).userFiredAction(actionInstance);
    }

    /**
     * Retrieves all updates for the thing since the last call of this method.
     * 
     * @param id
     *            The ID of the thing.
     * @return The updates since the last call.
     * @throws DatasourceFindException
     *             If the thing was not found.
     */
    public ThingUpdatesResponse getThingUpdates(long id) throws DatasourceFindException {
        return getBehavior(id).getUpdates();
    }

    /**
     * Share a thing with a user.
     *
     * @param thingId
     *            the thing id
     * @param userId
     *            the user id
     * @param permission
     *            the right
     * @throws GetPermissionException
     *             the get permission exception
     * @throws DatasourceInsertException
     *             the datasource insert exception
     */
    public void share(long thingId, long userId, ThingPermission permission) throws GetPermissionException, DatasourceInsertException {
        UserManagementFacade.getInstance().addNewPermissionToUser(userId, new Permission(permission.buildShiroPermission(thingId)));
    }

}
