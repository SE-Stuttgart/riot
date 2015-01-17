package de.uni_stuttgart.riot.db;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.junit.Test;

import de.uni_stuttgart.riot.rest.RiotApplication;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.remote.ActionDBObject;
import de.uni_stuttgart.riot.thing.remote.PropertyDBObject;
import de.uni_stuttgart.riot.thing.remote.RemoteThing;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

public class ThingDBTest extends JerseyDBTestBase{

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }
    
    private RemoteThing getTestRemoteThing() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        PropertyDBObjectSqlQueryDAO dao = new PropertyDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
        RemoteThingSqlQueryDAO daoT = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
        daoT.insert(new RemoteThing("Tes2t",1));
        return daoT.findBy(1);
    }
    
    @Test
    public void insertRemoteThingTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        RemoteThingSqlQueryDAO dao = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new RemoteThing("Tes2t",1));
        RemoteThing thing = dao.findBy(1);
    }
    
    @Test
    public void insertPropertyTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        PropertyDBObjectSqlQueryDAO dao = new PropertyDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
        RemoteThing thing = this.getTestRemoteThing();
        dao.insert(new PropertyDBObject("name", "v", "vt", thing.getId()));
        PropertyDBObject p = dao.findBy(1);
    }
    
    @Test 
    public void insertActionTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        ActionDBObjectSqlQueryDAO dao = new ActionDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
        Thing thing = this.getTestRemoteThing();
        dao.insert(new ActionDBObject("FactoryString"));
        ActionDBObject a = dao.findBy(1);
    }
    
    @Test
    public void insertRemoteThingAction() throws DatasourceInsertException, DatasourceFindException, SQLException, NamingException{
        Thing thing = this.getTestRemoteThing();
        ActionDBObject a = this.getTestAction();
        RemoteThingActionSqlQueryDAO dao = new RemoteThingActionSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new RemoteThingAction(thing.getId(),a.getId()));
        dao.findBy(1);
    }
    
    public ActionDBObject getTestAction() throws DatasourceFindException, DatasourceInsertException, SQLException, NamingException{
        ActionDBObjectSqlQueryDAO dao = new ActionDBObjectSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new ActionDBObject("FactoryString"));
        return dao.findBy(1);
    }
   

}
