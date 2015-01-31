package de.uni_stuttgart.riot.thing.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
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

    /**
     * gets the {@link RemoteThing} by its name.
     * 
     * @param name
     *            the thing name.
     * @return the {@link RemoteThing}.
     * @throws DatasourceFindException .
     */
    public RemoteThing getRemoteThing(String name) throws DatasourceFindException {
        return null;
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

    /**
     * gets the {@link RemoteThing} by its id.
     * 
     * @param id
     *            the id to be searched.
     * @return found {@link RemoteThing}
     * @throws DatasourceFindException
     *             if not found.
     */
    public RemoteThing getThing(long id) throws DatasourceFindException {
        RemoteThing remoteThing = this.remoteThingSqlQueryDAO.findBy(id);
        return this.completeRemoteThing(remoteThing);
    }

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

    /**
     * TODO .
     * 
     * @param userID
     * @return .
     * @throws DatasourceFindException
     */
    public Collection<RemoteThing> getAllThings(long userID) throws DatasourceFindException {
        return null;
    }

    public Queue<ActionInstance> getCurrentActionInstances() {
        return new ConcurrentLinkedQueue<ActionInstance>();
    }

    /**
     * TODO .
     * 
     * @param eventInstance
     */
    public void submitEvent(EventInstance eventInstance) {

    }

}
