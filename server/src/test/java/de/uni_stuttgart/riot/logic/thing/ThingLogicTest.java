package de.uni_stuttgart.riot.logic.thing;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Queue;

import javax.naming.NamingException;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEventInstance;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ThingLogicTest extends JerseyDBTestBase {

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new RiotApplication();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#getBaseUri()
     */
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("api/v1/").build();
    }
    
    @Test
    public void competionTest() throws DatasourceFindException{
        RemoteThingSqlQueryDAO daoT = new RemoteThingSqlQueryDAO();
        RemoteThing thing = daoT.findBy(1);
        ThingLogic logic = new ThingLogic();
        logic.completeRemoteThing(thing);
        assertEquals(2, thing.getActions().size());
        assertEquals(2, thing.getEvents().size());
        assertEquals(2, thing.getProperties().size());
    }
    
    @Test
    public void lastOnlinetest(){
        ThingLogic logic = new ThingLogic();
        Timestamp l1 = logic.lastConnection(1);
        assertEquals(l1, new Timestamp(0));
        logic.getCurrentActionInstances(1);
        Timestamp l2 = logic.lastConnection(1);
        System.out.println(l1);
        System.out.println(l2);
        assertEquals(true, l2.after(new Timestamp(System.currentTimeMillis() - 1000)));
    }
    
    @Test
    public void submitAndGetActionTest(){
        ThingLogic logic = new ThingLogic();
        Queue<ActionInstance> actions = logic.getCurrentActionInstances(1);
        assertEquals(0, actions.size());
        PropertySetAction<String> action = new PropertySetAction<String>("Test",1);
        PropertySetActionInstance<String> actionInstrance = action.createInstance(new Property<String>("Test","value"));
        logic.submitAction(actionInstrance);
        actions = logic.getCurrentActionInstances(1);
        assertEquals(1, actions.size());
        assertEquals(actionInstrance, actions.peek());
        actions = logic.getCurrentActionInstances(1);
        assertEquals(0, actions.size());
    }
    
    @Test
    public void eventTest(){
        ThingLogic logic = new ThingLogic();
        
    }

}
