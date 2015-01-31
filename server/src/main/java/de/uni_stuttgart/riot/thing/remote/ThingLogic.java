package de.uni_stuttgart.riot.thing.remote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.uni_stuttgart.riot.db.ActionDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.EventDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingActionSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingEventSqlQueryDAO;
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
public class ThingLogic {

    private RemoteThingSqlQueryDAO remoteThingSqlQueryDAO;
    private RemoteThingActionSqlQueryDAO remoteThingActionSqlQueryDAO;
    private PropertyDBObjectSqlQueryDAO propertySqlQueryDAO;
    private ActionDBObjectSqlQueryDAO actionDBObjectSqlQueryDAO;
    private EventDBObjectSqlQueryDAO eventDBObjectSqlQueryDAO;
    private RemoteThingEventSqlQueryDAO remoteThingEventSqlQueryDAO;

    /**
     * Constructor.
     */
    public ThingLogic() {
        this.remoteThingActionSqlQueryDAO = new RemoteThingActionSqlQueryDAO();
        this.remoteThingSqlQueryDAO = new RemoteThingSqlQueryDAO();
        this.propertySqlQueryDAO = new PropertyDBObjectSqlQueryDAO();
        this.actionDBObjectSqlQueryDAO = new ActionDBObjectSqlQueryDAO();
        this.eventDBObjectSqlQueryDAO = new EventDBObjectSqlQueryDAO();
        this.remoteThingEventSqlQueryDAO = new RemoteThingEventSqlQueryDAO();
        this.actionInstances = new HashMap<Long, Queue<ActionInstance>>();
        this.eventInstances = new HashMap<Long, Stack<EventInstance>>();
        this.events = new HashMap<Long, LinkedList<Event>>();
        this.lastConnection = new HashMap<Long, Timestamp>();
        
    }

    public long resolveID(String thingName) throws DatasourceFindException {
        return this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, thingName)).getId();
    }

    private HashMap<Long, Queue<ActionInstance>> actionInstances;
    private HashMap<Long, LinkedList<Event>> events;
    private HashMap<Long, Stack<EventInstance>> eventInstances;
    private HashMap<Long, Timestamp> lastConnection;

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
        this.remoteThingSqlQueryDAO.insert(thing);
        for (Action action : thing.getActions()) {
            ActionDBObject actionDBObject = new ActionDBObject(action.getFactoryString());
            this.actionDBObjectSqlQueryDAO.insert(actionDBObject);
            this.remoteThingActionSqlQueryDAO.insert(new RemoteThingAction(thing.getId(), actionDBObject.getId()));
        }
        for (iterable_type iterable_element : iterable) {
            
        }
        
        
        
        
        LinkedList<Event> thingEvents = ThingLogic.this.getEvents(thing.getId());
        thingEvents.addAll(thing.getEvents());
        this.lastConnection(thing.getId());
    }

    /**
     * TODO .
     * 
     * @param username
     * @param thingName
     * @throws DatasourceDeleteException 
     */
    public synchronized void unregisterThing(RemoteThing thing) throws DatasourceDeleteException {
        this.remoteThingSqlQueryDAO.delete(thing);
        this.events.put(thing.getId(), null);
    }

    public synchronized Queue<ActionInstance> getCurrentActionInstances(long thingId) {
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
    public synchronized void submitAction(ActionInstance actionInstance) {
        long thingId = actionInstance.getInstanceOf().getThingId(); 
        Queue<ActionInstance> queue = this.getActionInstancesQueue(thingId);
        queue.offer(actionInstance);
        this.updateLastConnection(thingId);
    }

    private synchronized Queue<ActionInstance> getActionInstancesQueue(long thingId) {
        Queue<ActionInstance> queue = this.actionInstances.get(thingId);
        if (queue == null) {
            Queue<ActionInstance> result = new LinkedList<ActionInstance>();
            this.actionInstances.put(thingId, result);
            return result;
        }
        return queue;
    }

    private synchronized Stack<EventInstance> getEventInstancesStack(long thingId) {
        Stack<EventInstance> stack = this.eventInstances.get(thingId);
        if (stack == null) {
            Stack<EventInstance> result = new Stack<EventInstance>();
            this.eventInstances.put(thingId, result);
            return result;
        }
        return stack;
    }

    /**
     * TODO .
     * 
     * @param eventInstance
     */
    public synchronized void submitEvent(EventInstance eventInstance) {
        LinkedList<Event> events = this.getEvents(eventInstance.getThingId());
        for (Event event : events) {
            if (event.isTypeOf(eventInstance)) {
                event.fire(eventInstance);
                break;
            }
        }
    }

    private synchronized LinkedList<Event> getEvents(long thingId) {
        LinkedList<Event> queue = this.events.get(thingId);
        if (queue == null) {
            LinkedList<Event> result = new LinkedList<Event>();
            this.events.put(thingId, result);
            return result;
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
        ArrayList<SearchParameter> searchParams = new ArrayList<SearchParameter>();
        searchParams.add(new SearchParameter(SearchFields.THINGID, remoteThing.getId()));
        Collection<PropertyDBObject> properties = this.propertySqlQueryDAO.findBy(searchParams, false);
        for (PropertyDBObject property : properties) {
            remoteThing.addProperty(property.getTheProperty(remoteThing));
        }
        Collection<RemoteThingAction> ras = this.remoteThingActionSqlQueryDAO.findBy(searchParams, false);
        for (RemoteThingAction ra : ras) {
            ActionDBObject aO = this.actionDBObjectSqlQueryDAO.findBy(ra.getActionID());
            remoteThing.addAction(aO.getTheAction(remoteThing));
        }
        Collection<RemoteThingEvent> res = this.remoteThingEventSqlQueryDAO.findBy(searchParams, false);
        for (RemoteThingEvent re : res) {
            EventDBObject aO = this.eventDBObjectSqlQueryDAO.findBy(re.getEventID());
            remoteThing.addEvent(aO.getTheEvent(remoteThing));
        }
        return remoteThing;
    }

    @SuppressWarnings("unchecked")
    public void deRegisterOnEvent(long thingId, final long registerOnthingId, Event event) {
        for (Event e : this.events.get(registerOnthingId)) {
            if(e.equals(event)) {
                e.unregister(thingId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void registerOnEvent(long thingId, final long registerOnthingId, Event event) {
        for (Event e : this.events.get(registerOnthingId)) {
            if (e.equals(event)) {
                e.register(new EventListener<EventInstance>(thingId) {
                    @Override
                    public void onFired(EventInstance event) {
                        ThingLogic.this.getEventInstancesStack(thingId).push(event);
                    }
                });
            }

        }
    }

    public Stack<EventInstance> getRegisteredEvents(long id) {
        Stack<EventInstance> currentEventInstances = this.eventInstances.get(id);
        Stack<EventInstance> result = this.eventInstances.get(id);
        Collections.copy(result, currentEventInstances);
        currentEventInstances.clear();
        return result;
    }

    public Timestamp lastConnection(long id) {
        Timestamp result = this.lastConnection.get(id);
        if (result == null)
            return new Timestamp(0);
        return result;
    }

}
