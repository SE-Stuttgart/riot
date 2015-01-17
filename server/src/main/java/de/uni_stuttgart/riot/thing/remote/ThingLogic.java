package de.uni_stuttgart.riot.thing.remote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.db.ActionDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingActionSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.ThingInfo;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;

public class ThingLogic {
    
    private RemoteThingSqlQueryDAO remoteThingSqlQueryDAO;
    private RemoteThingActionSqlQueryDAO remoteThingActionSqlQueryDAO;
    private PropertyDBObjectSqlQueryDAO propertySqlQueryDAO;
    private ActionDBObjectSqlQueryDAO actionDBObjectSqlQueryDAO;
    
    public ThingLogic() {
        try {
            this.remoteThingActionSqlQueryDAO = new RemoteThingActionSqlQueryDAO(ConnectionMgr.openConnection(), false);
            this.remoteThingSqlQueryDAO = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
            this.propertySqlQueryDAO = new PropertyDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
            this.actionDBObjectSqlQueryDAO = new ActionDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
    
    public RemoteThing getRemoteThing(String name) throws DatasourceFindException{
        RemoteThing remoteThing = this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, name));
        return this.completeRemoteThing(remoteThing);
    }
    
    private RemoteThing completeRemoteThing(RemoteThing remoteThing) throws DatasourceFindException{
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
    
    public RemoteThing getThing(long id) throws DatasourceFindException{
        RemoteThing remoteThing = this.remoteThingSqlQueryDAO.findBy(id);
        return this.completeRemoteThing(remoteThing);
    }
    
    public void registerThing(String username, String thingName, Collection<Property> properties, Collection<Action> actions){
        
    }
    
    public void unregisterThing(String username, String thingName){
        
    }
    
    public Collection<ThingInfo> getAllThings(long userID) throws DatasourceFindException{
        ArrayList<SearchParameter> searchParams = new ArrayList<SearchParameter>();
        searchParams.add(new SearchParameter(SearchFields.OWNERID, userID));
        Collection<RemoteThing> rThings = this.remoteThingSqlQueryDAO.findBy(searchParams, false);
        for (RemoteThing remoteThing : rThings) {
            this.completeRemoteThing(remoteThing);
        }
        Collection<ThingInfo> thingInfos = new ArrayList<ThingInfo>();
        for (RemoteThing remoteThing : rThings) {
            thingInfos.add(new ThingInfo(remoteThing.getName(), remoteThing.getProperties().values(), remoteThing.getActions()));
        }
        return thingInfos;
    }
    
    public Queue<ActionInstance> getCurrentActionInstances(){
        return new ConcurrentLinkedQueue<ActionInstance>();
    }
    
    public void submitEvent(EventInstance eventInstance){
        
    }

}
