package de.uni_stuttgart.riot.thing.remote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.db.PropertyDBObjectSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingActionSqlQueryDAO;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;

public class ThingLogic {
    
    private RemoteThingSqlQueryDAO remoteThingSqlQueryDAO;
    private RemoteThingActionSqlQueryDAO remoteThingActionSqlQueryDAO;
    private PropertyDBObjectSqlQueryDAO propertySqlQueryDAO;
    
    public ThingLogic() {
        try {
            this.remoteThingActionSqlQueryDAO = new RemoteThingActionSqlQueryDAO(ConnectionMgr.openConnection(),false);
            this.remoteThingSqlQueryDAO = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
            this.propertySqlQueryDAO = new PropertyDBObjectSqlQueryDAO(ConnectionMgr.openConnection(),false);
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
    
    public RemoteThing getRemoteThing(String name) throws DatasourceFindException{
        RemoteThing remoteThing = this.remoteThingSqlQueryDAO.findByUniqueField(new SearchParameter(SearchFields.NAME, name));
        return this.complRemoteThing(remoteThing);
    }
    
    private RemoteThing complRemoteThing(RemoteThing remoteThing) throws DatasourceFindException{
        ArrayList<SearchParameter> searchParams = new ArrayList<SearchParameter>();
        searchParams.add(new SearchParameter(SearchFields.THINGID, remoteThing.getId()));
        Collection<Property> properties = this.propertySqlQueryDAO.findBy(searchParams, false);
        for (Property property : properties) {
            remoteThing.addProperty(property);
        }
        return remoteThing;
    }
    
    public RemoteThing getRemoteThing(long id) throws DatasourceFindException{
        RemoteThing remoteThing = this.remoteThingSqlQueryDAO.findBy(id);
        return this.complRemoteThing(remoteThing);
    }
    
    public void registerThing(String username, String thingName){
        
    }
    
    public void unregisterThing(String username, String thingName){
        
    }
    
    public Queue<ActionInstance> getCurrentActionInstances(){
        return new ConcurrentLinkedQueue<ActionInstance>();
    }
    
    public void submitEvent(EventInstance eventInstance){
        
    }

}
