package de.uni_stuttgart.riot.db;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.remote.ActionDBObject;
import de.uni_stuttgart.riot.thing.remote.EventDBObject;
import de.uni_stuttgart.riot.thing.remote.PropertyDBObject;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql" })
public class ThingDBTest extends JerseyDBTestBase {

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }
    
    private RemoteThing getTestRemoteThing() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        RemoteThingSqlQueryDAO daoT = new RemoteThingSqlQueryDAO();
        return daoT.findBy(1);
    }

    @Test
    public void insertRemoteThingTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        RemoteThingSqlQueryDAO dao = new RemoteThingSqlQueryDAO();
        dao.insert(new RemoteThing("Tes2t", 1));
        RemoteThing thing = dao.findBy(4);
    }

    @Test
    public void insertPropertyTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        PropertyDBObjectSqlQueryDAO dao = new PropertyDBObjectSqlQueryDAO();
        RemoteThing thing = this.getTestRemoteThing();
        dao.insert(new PropertyDBObject("name", "v", "vt", thing.getId()));
        PropertyDBObject p = dao.findBy(4);
    }

    @Test
    public void insertActionTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        ActionDBObjectSqlQueryDAO dao = new ActionDBObjectSqlQueryDAO();
        dao.insert(new ActionDBObject("FactoryString"));
        ActionDBObject a = dao.findBy(4);
    }
    
    @Test
    public void insertEventTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {
        EventDBObjectSqlQueryDAO dao = new EventDBObjectSqlQueryDAO();
        dao.insert(new EventDBObject("Test"));
        EventDBObject a = dao.findBy(4);
    }

    @Test
    public void insertRemoteThingAction() throws DatasourceInsertException, DatasourceFindException, SQLException, NamingException {
        Thing thing = this.getTestRemoteThing();
        ActionDBObject a = this.getTestAction();
        RemoteThingActionSqlQueryDAO dao = new RemoteThingActionSqlQueryDAO();
        dao.insert(new RemoteThingAction(thing.getId(), a.getId()));
        dao.findBy(1);
    }
    
    @Test
    public void insertRemoteThingEvent() throws DatasourceInsertException, DatasourceFindException, SQLException, NamingException {
        RemoteThingActionSqlQueryDAO dao = new RemoteThingActionSqlQueryDAO();
        dao.insert(new RemoteThingAction(1, 1));
        dao.findBy(4);
    }

    public ActionDBObject getTestAction() throws DatasourceFindException, DatasourceInsertException, SQLException, NamingException {
        ActionDBObjectSqlQueryDAO dao = new ActionDBObjectSqlQueryDAO();
        dao.insert(new ActionDBObject("FactoryString"));
        return dao.findBy(1);
    }

}
