package de.uni_stuttgart.riot.thing.remote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.db.thing.ActionDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.thing.EventDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.thing.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.thing.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventListener;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * Contains all logic regarding to {@link Thing} handling.
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ThingLogic {

    private static ThingLogic instance;
    
    private RemoteThingSqlQueryDAO remoteThingSqlQueryDAO;
    private PropertyDBObjectSqlQueryDAO propertySqlQueryDAO;
    private ActionDBObjectSqlQueryDAO actionDBObjectSqlQueryDAO;
    private EventDBObjectSqlQueryDAO eventDBObjectSqlQueryDAO;

    private HashMap<Long, Queue<ActionInstance>> actionInstances;
    private HashMap<Long, LinkedList<Event>> events;
    private HashMap<Long, Stack<EventInstance>> eventInstances;
    private HashMap<Long, Timestamp> lastConnection;

    private ThingLogic() throws DatasourceFindException {
        this.remoteThingSqlQueryDAO = new RemoteThingSqlQueryDAO();
        this.propertySqlQueryDAO = new PropertyDBObjectSqlQueryDAO();
        this.actionDBObjectSqlQueryDAO = new ActionDBObjectSqlQueryDAO();
        this.eventDBObjectSqlQueryDAO = new EventDBObjectSqlQueryDAO();
        this.actionInstances = new HashMap<Long, Queue<ActionInstance>>();
        this.eventInstances = new HashMap<Long, Stack<EventInstance>>();
        this.events = new HashMap<Long, LinkedList<Event>>();
        this.lastConnection = new HashMap<Long, Timestamp>();
        this.initStoredThings();
    }
    
    /**
     * Getter for {@link ThingLogic}.
     * @return
     *      Instance of {@link ThingLogic}
     * @throws DatasourceFindException
     *      Exception on initialization of Thing due to datasource exception.
     */
    public static ThingLogic getThingLogic() throws DatasourceFindException {
        if (instance == null) {
            instance = new ThingLogic();
        }
        return instance;
    }

    private void initStoredThings() throws DatasourceFindException {
        Collection<RemoteThing> things = this.remoteThingSqlQueryDAO.findAll();
        for (RemoteThing remoteThing : things) {
            this.completeRemoteThing(remoteThing);
            this.internalRegister(remoteThing);
        }
    }

    /**
     * Resolves the thing id by from a given thing name.
     * @param thingName
     *      name of a thing
     * @return
     *      unique thing id
     * @throws DatasourceFindException
     *      if there is no thing with name thingname
     */
    public long resolveID(String thingName) throws DatasourceFindException {
        return this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, thingName)).getId();
    }

    /**
     * Registers a thing in the thing logic. Includes storing the thing in the datasource.
     * This operation must be called once for every thing to be used in any way regarding server operations.
     * @param thing
     *      Thing to be registered.
     * @throws DatasourceInsertException
     *      Exception during insertion of the thing into the datasource
     */
    public synchronized void registerThing(final RemoteThing thing) throws DatasourceInsertException {
        try {
            this.remoteThingSqlQueryDAO.insert(thing);
            UserManagementFacade facade = UserManagementFacade.getInstance();
            facade.addNewPermissionToUser(thing.getOwnerID(), new Permission("thing:" + thing.getId() + ":*"));
            for (Action action : thing.getActions()) {
                String actionString = JsonUtil.getJsonUtil().toJson(action);
                ActionDBObject actionDBObject = new ActionDBObject(thing.getId(), actionString);
                this.actionDBObjectSqlQueryDAO.insert(actionDBObject);
            }
            for (Event event : thing.getEvents()) {
                String eventString = JsonUtil.getJsonUtil().toJson(event);
                EventDBObject eventDBObject = new EventDBObject(thing.getId(), eventString);
                this.eventDBObjectSqlQueryDAO.insert(eventDBObject);
            }
            for (Property property : thing.getProperties().values()) {
                String propertyValueString = JsonUtil.getJsonUtil().toJson(property.getValue());
                PropertyDBObject propertyDBObject = new PropertyDBObject(property.getName(), propertyValueString, property.getValue().getClass().getCanonicalName(), thing.getId());
                this.propertySqlQueryDAO.insert(propertyDBObject);
            }
            internalRegister(thing);
            this.lastConnection(thing.getId());
        } catch (Exception e) {
            throw new DatasourceInsertException(e);
        }
    }

    private void internalRegister(final RemoteThing thing) throws DatasourceFindException {
        this.events.put(thing.getId(), new LinkedList<Event>());
        this.actionInstances.put(thing.getId(), new LinkedList<ActionInstance>());
        LinkedList<Event> thingEvents = this.getEvents(thing.getId());
        thingEvents.addAll(thing.getEvents());
    }

    /**
     * Unregisters a thing. Includes removal of the thing from the datasource.
     * @param id
     *      thing id of the thing to be unregistered.
     * @throws DatasourceDeleteException
     *      Exception during removal form datasource, e.g. no thing with given id exists in the datasource
     */
    public synchronized void unregisterThing(long id) throws DatasourceDeleteException {
        this.events.put(id, null);
        this.actionInstances.put(id, null);
        this.remoteThingSqlQueryDAO.delete(id);
    }

    /**
     * Returns all {@link ActionInstance}s that had been submitted for the thing with id thingid (since last call).
     * @param thingId
     *      id of thing
     * @return
     *      actionInstacnes that had been submitted
     * @throws DatasourceFindException
     *      If thing with given id is not registered.
     *      
     */
    public synchronized Queue<ActionInstance> getCurrentActionInstances(long thingId) throws DatasourceFindException {
        Queue<ActionInstance> queue = this.getActionInstancesQueue(thingId);
        Queue<ActionInstance> result = new LinkedList<ActionInstance>();
        while (!queue.isEmpty()) {
            result.offer(queue.poll());
        }
        this.updateLastConnection(thingId);
        return result;
    }

    private void updateLastConnection(long thingId) {
        this.lastConnection.put(thingId, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Submits the given {@link ActionInstance} to the thing with id  {@link ActionInstance#getThingId()}.
     * So that the result of calling {@link ThingLogic#getCurrentActionInstances()} with {@link ActionInstance#getThingId()}
     * would include the given a actionInstance.
     * @param actionInstance
     *      {@link ActionInstance} that should be submitted
     * @throws DatasourceFindException
     *      if there is no thing with {@link ActionInstance#getThingId()} registered
     */
    public synchronized void submitAction(ActionInstance actionInstance) throws DatasourceFindException {
        //FIXME Racecondition ?
        long thingId = actionInstance.getThingId();
        Queue<ActionInstance> queue = this.getActionInstancesQueue(thingId);
        queue.offer(actionInstance);
        this.updateLastConnection(thingId);
    }

    private synchronized Queue<ActionInstance> getActionInstancesQueue(long thingId) throws DatasourceFindException {
        Queue<ActionInstance> queue = this.actionInstances.get(thingId);
        if (queue == null) {
            throw new DatasourceFindException("Thing with id :" + thingId + "could not be found");
        }
        return queue;
    }

    private synchronized Stack<EventInstance> getEventInstancesStack(long thingId) throws DatasourceFindException {
        Stack<EventInstance> stack = this.eventInstances.get(thingId);
        if (stack == null) {
            this.eventInstances.put(thingId, new Stack<EventInstance>());
        }
        return this.eventInstances.get(thingId);
    }

    /**
     * Submits a {@link EventInstance} to the server so that every thing that was registered on that (event,thing) 
     * will receive the instance by calling {@link ThingLogic#getRegisteredEvents(long)}.
     * @param eventInstance
     *      {@link EventInstance} to be submitted.
     * @throws DatasourceFindException
     *      if there is no thing with {@link EventInstance#getThingId()} registered
     */
    public synchronized void submitEvent(EventInstance eventInstance) throws DatasourceFindException {
        LinkedList<Event> eventL = this.getEvents(eventInstance.getThingId());
        for (Event event : eventL) {
            if (event.isTypeOf(eventInstance)) {
                event.fire(eventInstance);
                break;
            }
        }
    }

    private synchronized LinkedList<Event> getEvents(long thingId) throws DatasourceFindException {
        LinkedList<Event> queue = this.events.get(thingId);
        if (queue == null) {
            throw new DatasourceFindException("Thing with id :" + thingId + "could not be found");
        }
        return queue;
    }

    /**
     * Fills the object {@link RemoteThing} with its properties and actions.
     * 
     * @param remoteThing
     *            to be filled.
     * @return the filled object.
     * @throws DatasourceFindException .
     */
    public RemoteThing completeRemoteThing(RemoteThing remoteThing) throws DatasourceFindException {
        try {
            ArrayList<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.THINGID, remoteThing.getId()));
            Collection<PropertyDBObject> properties = this.propertySqlQueryDAO.findBy(searchParams, false);
            for (PropertyDBObject property : properties) {
                Class clazz = Class.forName(property.getValType());
                Property p = new Property(property.getName(), JsonUtil.getJsonUtil().toObject(property.getVal(), clazz));
                remoteThing.addProperty(p);
            }
            Collection<ActionDBObject> actions = this.actionDBObjectSqlQueryDAO.findBy(searchParams, false);
            for (ActionDBObject action : actions) {
                Action a = JsonUtil.getJsonUtil().toObject(action.getFactoryString(), Action.class);
                remoteThing.addAction(a);
            }
            Collection<EventDBObject> eventL = this.eventDBObjectSqlQueryDAO.findBy(searchParams, false);
            for (EventDBObject event : eventL) {
                Event e = JsonUtil.getJsonUtil().toObject(event.getFactoryString(), Event.class);
                remoteThing.addEvent(e);
            }
            return remoteThing;
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * Deregisters the given thing (thingID) from the event (event) of thing (registerOnthingId).
     * @param thingId
     *      thing that wants to deregister.
     * @param registerOnthingId
     *     thing
     * @param event
     *     event
     * @throws DatasourceFindException
     *      if there is no thing with thingId or registerOnthingId registered
     *      
     */
    public synchronized void deRegisterOnEvent(long thingId, final long registerOnthingId, Event event) throws DatasourceFindException {
        for (Event e : this.getEvents(registerOnthingId)) {
            if (e.equals(event)) {
                e.unregister(thingId);
            }
        }
    }

    /**
     * Registers the given thing (thingid) on the event of thing with id registerOnthingId.
     * @param thingId
     *      thing that wants to register
     * @param registerOnthingId
     *      registerOnthingId
     * @param event
     *          event
     * @throws DatasourceFindException
     *      if there is no thing with thingId or registerOnthingId registered
     */
    public synchronized void registerOnEvent(long thingId, final long registerOnthingId, Event event) throws DatasourceFindException {
        final Stack<EventInstance> eIStack = this.getEventInstancesStack(thingId);
        for (Event e : this.getEvents(registerOnthingId)) {
            if (e.equals(event)) {
                e.register(new EventListener<EventInstance>(thingId) {
                    @Override
                    public void onFired(EventInstance event) {
                        eIStack.push(event);
                    }
                });
            }
        }
    }

    /**
     * Returns all eventinstances that had been submitted and were thing with id is registered on 
     * since the last call.
     * @param id
     *      id of the thing that wants to retrive his eventinstances.
     * @return
     *      the eventInstances
     * @throws DatasourceFindException
     *      if there is no thing with thingId or registerOnthingId registered
     */
    public synchronized Stack<EventInstance> getRegisteredEvents(long id) throws DatasourceFindException {
        Stack<EventInstance> currentEventInstances = this.getEventInstancesStack(id);
        Stack<EventInstance> result = new Stack<EventInstance>();
        result.addAll(currentEventInstances);
        currentEventInstances.clear();
        return result;
    }

    /**
     * Returns the last time the given thing has called the server.
     * Returns 1.1.1970 if the thing has never called the server.
     * @param id
     *      thing id
     * @return
     *      last server call time
     */
    public synchronized Timestamp lastConnection(long id) {
        Timestamp result = this.lastConnection.get(id);
        if (result == null) {
            return new Timestamp(0);
        }
        return result;
    }

}
