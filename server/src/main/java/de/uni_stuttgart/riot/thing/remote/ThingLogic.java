package de.uni_stuttgart.riot.thing.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.uni_stuttgart.riot.db.ActionDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingActionSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;

/**
 * Contains all logic regarding to {@link Thing}.
 *
 */
public class ThingLogic {

    private RemoteThingSqlQueryDAO remoteThingSqlQueryDAO;
    private RemoteThingActionSqlQueryDAO remoteThingActionSqlQueryDAO;
    private PropertyDBObjectSqlQueryDAO propertySqlQueryDAO;
    private ActionDBObjectSqlQueryDAO actionDBObjectSqlQueryDAO;

    /**
     * Constructor.
     */
    public ThingLogic() {
        this.remoteThingActionSqlQueryDAO = new RemoteThingActionSqlQueryDAO();
        this.remoteThingSqlQueryDAO = new RemoteThingSqlQueryDAO();
        this.propertySqlQueryDAO = new PropertyDBObjectSqlQueryDAO();
        this.actionDBObjectSqlQueryDAO = new ActionDBObjectSqlQueryDAO();
    }

    public long resolveID(String thingName) throws DatasourceFindException{
        return this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, thingName)).getId();
    }
    
    private HashMap<Long, Queue<ActionInstance>> actionInstances;
    private HashMap<Long, Stack<EventInstance>> eventInstances;


    /**
     * TODO .
     * 
     * @param username
     * @param thingName
     * @param properties
     * @param actions
     */
    public void registerThing(String username, String thingName, Collection<Property> properties, Collection<Action> actions) {

    }

    /**
     * TODO .
     * 
     * @param username
     * @param thingName
     */
    public void unregisterThing(String username, String thingName) {

    }

    public synchronized Queue<ActionInstance> getCurrentActionInstances(long thingId) {
        Queue<ActionInstance> queue = this.getActionInstancesQueue(thingId);
        Queue<ActionInstance> result = new LinkedList<ActionInstance>();
        while (!queue.isEmpty()) {
            result.offer(queue.poll());
        }
        return result;
    }

         //FIXME thing about raceconditions on, off -> off, on
    public synchronized void submitAction(ActionInstance actionInstance) {
        Queue<ActionInstance> queue = this.getActionInstancesQueue(actionInstance.getInstanceOf().getThingId());
        queue.offer(actionInstance);        
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

    /**
     * TODO .
     * 
     * @param eventInstance
     */
    public synchronized void submitEvent(EventInstance eventInstance) {
        Stack<EventInstance> stack = this.getEventInstancesQueue(eventInstance.getThingId());
        stack.push(eventInstance);   
    }

    private synchronized Stack<EventInstance> getEventInstancesQueue(long thingId) {
        Stack<EventInstance> queue = this.eventInstances.get(thingId);
        if (queue == null) {
            Stack<EventInstance> result = new Stack<EventInstance>();
            this.eventInstances.put(thingId, result);
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
        return remoteThing;
    }

    public Stack<EventInstance> getEvents() {
        // TODO Auto-generated method stub
        return null;
    }

  
}
