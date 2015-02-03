package de.uni_stuttgart.riot.thing.remote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import de.uni_stuttgart.riot.db.ActionDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.EventDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
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

/**
 * Contains all logic regarding to {@link Thing}.
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

    public static ThingLogic getThingLogic() throws DatasourceFindException {
        if (instance == null) {
            instance = new ThingLogic();
        }
        return instance;
    }

    /**
     * Constructor.
     * 
     * @throws DatasourceFindException
     */
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

    private void initStoredThings() throws DatasourceFindException {
        Collection<RemoteThing> things = this.remoteThingSqlQueryDAO.findAll();
        for (RemoteThing remoteThing : things) {
            this.completeRemoteThing(remoteThing);
            this.internalRegister(remoteThing);
        }
    }

    public long resolveID(String thingName) throws DatasourceFindException {
        return this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, thingName)).getId();
    }

    /**
     * TODO .
     * 
     * @param username
     * @param thingName
     * @param properties
     * @param actions
     * @throws DatasourceInsertException
     */
    public synchronized void registerThing(final RemoteThing thing) throws DatasourceInsertException {
        try {
            this.remoteThingSqlQueryDAO.insert(thing);
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
     * TODO .
     * 
     * @param username
     * @param thingName
     * @throws DatasourceDeleteException
     */
    public synchronized void unregisterThing(long id) throws DatasourceDeleteException {
        this.events.put(id, null);
        this.actionInstances.put(id, null);
        this.remoteThingSqlQueryDAO.delete(id);
    }

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

    // FIXME thing about raceconditions on, off -> off, on
    public synchronized void submitAction(ActionInstance actionInstance) throws DatasourceFindException {
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
     * TODO .
     * 
     * @param eventInstance
     * @throws DatasourceFindException
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

    public void deRegisterOnEvent(long thingId, final long registerOnthingId, Event event) throws DatasourceFindException {
        for (Event e : this.getEvents(registerOnthingId)) {
            if (e.equals(event)) {
                e.unregister(thingId);
            }
        }
    }

    public void registerOnEvent(long thingId, final long registerOnthingId, Event event) throws DatasourceFindException {
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

    public Stack<EventInstance> getRegisteredEvents(long id) throws DatasourceFindException {
        Stack<EventInstance> currentEventInstances = this.getEventInstancesStack(id);
        Stack<EventInstance> result = new Stack<EventInstance>();
        result.addAll(currentEventInstances);
        currentEventInstances.clear();
        return result;
    }

    public Timestamp lastConnection(long id) {
        Timestamp result = this.lastConnection.get(id);
        if (result == null) {
            return new Timestamp(0);
        }
        return result;
    }

}
