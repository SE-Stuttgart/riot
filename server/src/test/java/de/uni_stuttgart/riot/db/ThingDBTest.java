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
import de.uni_stuttgart.riot.thing.remote.RemoteThing;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

public class ThingDBTest extends JerseyDBTestBase{

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }
    
    private RemoteThing getTestRemoteThing() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        PropertySqlQueryDAO dao = new PropertySqlQueryDAO(ConnectionMgr.openConnection(), false);
        RemoteThingSqlQueryDAO daoT = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
        daoT.insert(new RemoteThing("Tes2t"));
        return daoT.findBy(1);
    }
    
    @Test
    public void insertRemoteThingTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        RemoteThingSqlQueryDAO dao = new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new RemoteThing("Tes2t"));
        RemoteThing thing = dao.findBy(1);
    }
    
    @Test
    public void insertPropertyTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        PropertySqlQueryDAO dao = new PropertySqlQueryDAO(ConnectionMgr.openConnection(), false);
        RemoteThing thing = this.getTestRemoteThing();
        dao.insert(new Property<String>("TestName","Test",thing));
        Property<String> p = dao.findBy(1);
        System.out.println(p);
    }
    
    @Test 
    public void insertActionTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException{
        ActionSqlQueryDAO dao = new ActionSqlQueryDAO(ConnectionMgr.openConnection(), false);
        Thing thing = this.getTestRemoteThing();
        dao.insert(new PropertySetAction<String>("test", thing));
        Action a = dao.findBy(1);
    }
    
    @Test
    public void insertRemoteThingAction() throws DatasourceInsertException, DatasourceFindException, SQLException, NamingException{
        Thing thing = this.getTestRemoteThing();
        Action a = this.getTestAction();
        RemoteThingActionSqlQueryDAO dao = new RemoteThingActionSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new RemoteThingAction(thing.getId(),a.getId()));
        dao.findBy(1);
    }
    
    public Action getTestAction() throws DatasourceFindException, DatasourceInsertException, SQLException, NamingException{
        ActionSqlQueryDAO dao = new ActionSqlQueryDAO(ConnectionMgr.openConnection(), false);
        dao.insert(new PropertySetAction<String>("test", null));
        return dao.findBy(1);
    }
   

}
