package de.uni_stuttgart.riot.thing.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.db.thing.ThingDAO;
import de.uni_stuttgart.riot.db.thing.ThingUserSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingMetainfo;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;
import de.uni_stuttgart.riot.thing.rest.UserThingShare;
import de.uni_stuttgart.riot.thing.rest.ThingInformation.Field;
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
     * The User Management Facade.
     */
    private final UserManagementFacade umFacade = UserManagementFacade.getInstance();

    /**
     * The DAO for storing sharing information.
     */
    private final ThingUserSqlQueryDAO thingUserDAO = new ThingUserSqlQueryDAO();

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
        public ServerThingBehavior newBehavior(Class<? extends Thing> thingType) {
            return new ServerThingBehavior();
        }

        public ServerThingBehavior existingBehavior(long thingID) {
            return things.get(thingID);
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
     * Gets a stream of all things that a user can access.
     * 
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @param permission
     *            The permission that the user must have for the things to be returned. If this is <tt>null</tt>, all things will be
     *            returned.
     * @return The matching things.
     */
    private Stream<Thing> getThingStream(Long userId, ThingPermission permission) {
        if (permission == null) {
            return things.values().stream().map(ThingBehavior::getThing);
        } else {
            long finalUserId = (userId == null) ? umFacade.getCurrentUserId() : userId;
            return things.values().stream().filter((behavior) -> behavior.canAccess(finalUserId, permission)).map(ThingBehavior::getThing);
        }
    }

    /**
     * Returns a filtered collection of those things that the current user can access.
     * 
     * @param filter
     *            The filter.
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @param requirePermission
     *            May be null. If set, only the things will be considered that the current user has the given permission for.
     * @return The things that match the filter.
     */
    public Stream<Thing> findThings(FilteredRequest filter, Long userId, ThingPermission requirePermission) {
        Stream<Thing> stream = getThingStream(userId, requirePermission);

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
        return stream;
    }

    /**
     * Returns a paginated collection of things.
     * 
     * @param offset
     *            The offset (first index to be returned).
     * @param limit
     *            The number of things to be returned.
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @param requirePermission
     *            May be null. If set, only the things will be considered that the current user has the given permission for.
     * @return The matching collection of things.
     */
    public Stream<Thing> findThings(int offset, int limit, Long userId, ThingPermission requirePermission) {
        Stream<Thing> stream = getThingStream(userId, requirePermission).skip(offset);
        if (limit > 0) {
            stream = stream.limit(limit);
        }
        return stream;
    }

    /**
     * Returns a collection of all known things.
     * 
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @param requirePermission
     *            May be null. If set, only the things will be considered that the current user has the given permission for.
     * @return All things.
     */
    public Stream<Thing> getAllThings(Long userId, ThingPermission requirePermission) {
        return getThingStream(userId, requirePermission);
    }

    /**
     * Registers a thing in the thing logic. Includes storing the thing in the datasource. This operation must be called once for every
     * thing to be used in any way regarding server operations.
     * 
     * @param thingType
     *            The type of the thing to be registered.
     * @param metainfo
     *            The metainfo of the thing. This must be well-checked for security vulnerabilities, etc.! Or it may be <tt>null</tt>.
     * @return The newly registered Thing.
     * @throws DatasourceInsertException
     *             Exception during creation of the thing or during insertion of the thing into the datasource
     */
    public synchronized Thing registerThing(String thingType, ThingMetainfo metainfo) throws DatasourceInsertException {
        try {
            ServerThingBehavior behavior = ThingFactory.create(thingType, 0, behaviorFactory);
            Thing thing = behavior.getThing();
            if (metainfo != null) {
                metainfo.apply(thing);
            }
            thingDAO.insert(thing);
            things.put(thing.getId(), behavior); // The behaviorFactory didn't add it because the ID was missing.
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
     * Adds or updates the permission on the thing for the user. If the permissions are empty, all permissions will be revoked and the share
     * will be deleted. Existing permissions will be replaced.
     * 
     * @param thingId
     *            The ID of the thing.
     * @param share
     *            The thing share containing the user and his/her permissions.
     * @throws DatasourceFindException
     *             If the given thing does not exist.
     * @throws DatasourceInsertException
     *             When storing the information fails.
     * @throws DatasourceDeleteException
     *             When storing the information fails.
     */
    public void addOrUpdateShare(long thingId, ThingShare share) throws DatasourceFindException, DatasourceDeleteException, DatasourceInsertException {
        getBehavior(thingId).addOrUpdateShare(share);
        thingUserDAO.saveThingShare(thingId, share);
    }

    /**
     * Revokes all thing access permissions from a user for a certain thing.
     *
     * @param thingId
     *            the thing id
     * @param userId
     *            The user id.
     * @throws DatasourceDeleteException
     *             When storing the information fails.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    public void unshare(long thingId, long userId) throws DatasourceDeleteException, DatasourceFindException {
        getBehavior(thingId).removeShare(userId);
        thingUserDAO.deleteThingShare(thingId, userId);
    }

    /**
     * Determines if the given user has the right to access the given thing.
     * 
     * @param thingId
     *            The thing id.
     * @param userId
     *            The user id. If this is <tt>null</tt>, it will be replaced with the current user's id.
     * @param permission
     *            The permission to check for.
     * @return True if the access is permitted, false otherwise.
     * @throws DatasourceFindException
     *             If a thing with the given id does not exist.
     */
    public boolean canAccess(long thingId, Long userId, ThingPermission permission) throws DatasourceFindException {
        return getBehavior(thingId).canAccess(userId == null ? umFacade.getCurrentUserId() : userId, permission);
    }

    /**
     * Checks whether the given user is the owner of the given thing.
     * 
     * @param thingId
     *            The thing id.
     * @param userId
     *            The user id. If this is <tt>null</tt>, it will be replaced with the current user's id.
     * @return True if the user is the owner, false otherwise.
     * @throws DatasourceFindException
     *             If a thing with the given id does not exist.
     */
    public boolean isOwner(long thingId, Long userId) throws DatasourceFindException {
        Long actualOwner = getBehavior(thingId).getThing().getOwnerId();
        return actualOwner.equals(userId == null ? umFacade.getCurrentUserId() : userId);
    }

    /**
     * Gets the permissions of all users on the given thing (directly, excluding parent permissions).
     * 
     * @param thingId
     *            The thing id.
     * @return A list of thing shares, each of which contains the user id and the permissions that the user has.
     * @throws DatasourceFindException
     *             If a thing with the given id does not exist.
     */
    public Collection<ThingShare> getThingShares(long thingId) throws DatasourceFindException {
        return getBehavior(thingId).getShares();
    }

    /**
     * Gets the permissions of all users on the given thing (directly, excluding parent permissions), where the users are resolved to full
     * {@link User} objects.
     * 
     * @param thingId
     *            The thing id.
     * @return A list of thing shares, each of which contains the user and the permissions that the user has.
     * @throws DatasourceFindException
     *             If a thing with the given id does not exist.
     */
    public Collection<UserThingShare> getUserThingShares(long thingId) throws DatasourceFindException {
        return getBehavior(thingId).getShares().stream().map((share) -> {
            try {
                return new UserThingShare(umFacade.getUser(share.getUserId()), share.getPermissions());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Gets all children of a thing (according to their {@link Thing#getParentReference()}). Note that permissions are inherited, so that
     * any user can access the things if he/she can access the parent.
     * 
     * @param parent
     *            The parent thing.
     * @return A collection of all things, never <tt>null</tt>.
     */
    public Collection<Thing> getChildren(Thing parent) {
        Objects.requireNonNull(parent, "parent must not be null");
        return getChildren(parent.getId());
    }

    /**
     * Gets all children of a thing (according to their {@link Thing#getParentReference()}). Note that permissions are inherited, so that
     * any user can access the things if he/she can access the parent.
     * 
     * @param parentId
     *            The id of the parent thing.
     * @return A collection of all things, never <tt>null</tt>.
     */
    public Collection<Thing> getChildren(Long parentId) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        return things.values().stream().map(ThingBehavior::getThing).filter((thing) -> {
            return parentId.equals(thing.getParentId());
        }).collect(Collectors.toList());
    }

    /**
     * Sets the parent reference of the <tt>child</tt> thing to the <tt>parent</tt> thing and persists the change in the database.
     * 
     * @param child
     *            The child thing, must not be <tt>null</tt>.
     * @param parent
     *            The parent thing, may be <tt>null</tt> to unset the parent.
     * @throws DatasourceUpdateException
     *             When storing the information fails.
     */
    public void setParent(Thing child, Thing parent) throws DatasourceUpdateException {
        Objects.requireNonNull(child, "child must not be null");
        child.setParent(parent);
        thingDAO.update(child);
    }

    /**
     * Sets the metainfo of the <tt>thing</tt> and persists the changes in the database. This way, it is possible to change the owner,
     * parent and/or name of a Thing.
     * 
     * @param thing
     *            The thing to write to.
     * @param metainfo
     *            The metainfo to read from.
     * @throws DatasourceUpdateException
     *             When storing the information fails.
     */
    public void setMetainfo(Thing thing, ThingMetainfo metainfo) throws DatasourceUpdateException {
        if (metainfo.getName() == null || metainfo.getName().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty!");
        } else if (metainfo.getOwnerId() == null || metainfo.getOwnerId() < 1) {
            throw new IllegalArgumentException("ownerId must not be empty!");
        } else if (metainfo.getParentId() == null || metainfo.getParentId() < 1) {
            metainfo.setParentId(null);
        }

        Objects.requireNonNull(thing, "thing must not be null");
        metainfo.apply(thing);
        thingDAO.update(thing);
    }

    /**
     * Maps the given Thing to a ThingInformation representation containing the given fields (the rest of the fields will be <tt>null</tt>).
     * 
     * @param userId
     *            The user to get the information for (will be replaced by current user, if <tt>null</tt>). Information that the user must
     *            not read will be excluded. Please check first that the user can access the <tt>thing</tt> itself.
     * @param thing
     *            The thing to be mapped.
     * @param fields
     *            The fields to include.
     * @return The mapped thing.
     */
    public ThingInformation map(Long userId, Thing thing, Collection<Field> fields) { // NOCS
        if (thing == null) {
            return null;
        } else if (fields == null || fields.isEmpty()) {
            return new ThingInformation();
        }

        ThingInformation result = new ThingInformation();
        result.setId(thing.getId());
        result.setType(thing.getClass().getName());

        Long eUserId = userId;

        for (Field field : fields) {
            switch (field) {
            case METAINFO:
                result.setMetainfo(ThingMetainfo.create(thing));
                break;

            case STATE:
                result.setState(ThingState.create(thing));
                break;

            case DESCRIPTION:
                result.setDescription(ThingDescription.create(thing));
                break;

            case DIRECTCHILDREN:
                if (fields.contains(Field.ALLCHILDREN)) {
                    continue;
                }
                // Fall through
            case ALLCHILDREN:
                Set<Field> newFields = new HashSet<>(fields);
                newFields.remove(Field.DIRECTCHILDREN);
                List<ThingInformation> childInfos = new ArrayList<>();
                for (Thing child : getChildren(thing)) {
                    childInfos.add(map(eUserId, child, newFields));
                }
                result.setChildren(childInfos);
                break;

            case SHARES:
                if (eUserId == null || eUserId.equals(0)) {
                    eUserId = umFacade.getCurrentUserId();
                }
                try {
                    if (canAccess(thing.getId(), eUserId, ThingPermission.SHARE)) {
                        result.setShares(getThingShares(thing.getId()));
                    }
                } catch (DatasourceFindException e) {
                    // Ignore.
                }
                break;

            case LASTCONNECTION:
                try {
                    result.setLastConnection(getLastConnection(thing.getId()));
                } catch (DatasourceFindException e) {
                    // Ignore.
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported field type " + field);
            }
        }
        return result;
    }

}
