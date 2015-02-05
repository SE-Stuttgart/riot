package de.uni_stuttgart.riot.logic.thing;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.Stack;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.db.thing.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;
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
        return new RiotApplication(false);
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
    public void competionTest() throws DatasourceFindException {
        RemoteThingSqlQueryDAO daoT = new RemoteThingSqlQueryDAO();
        RemoteThing thing = daoT.findBy(1);
        ThingLogic logic = ThingLogic.getThingLogic();
        logic.completeRemoteThing(thing);
        assertEquals(2, thing.getActions().size());
        assertEquals(2, thing.getEvents().size());
        assertEquals(2, thing.getProperties().size());
    }

    @Test
    public void lastOnlinetest() throws DatasourceFindException {
        ThingLogic logic = ThingLogic.getThingLogic();
        Timestamp l1 = logic.lastConnection(1);
        assertEquals(l1, new Timestamp(0));
        logic.getCurrentActionInstances(1);
        Timestamp l2 = logic.lastConnection(1);
        assertEquals(true, l2.after(new Timestamp(System.currentTimeMillis() - 1000)));
    }

    @Test
    public void submitAndGetActionTest() throws DatasourceFindException {
        ThingLogic logic = ThingLogic.getThingLogic();
        Queue<ActionInstance> actions = logic.getCurrentActionInstances(1);
        assertEquals(0, actions.size());
        PropertySetAction<String> action = new PropertySetAction<String>("Test");
        PropertySetActionInstance<String> actionInstrance = action.createInstance("value", 1);
        logic.submitAction(actionInstrance);
        actions = logic.getCurrentActionInstances(1);
        assertEquals(1, actions.size());
        assertEquals(actionInstrance, actions.peek());
        actions = logic.getCurrentActionInstances(1);
        assertEquals(0, actions.size());
    }

    @Test
    public void eventTest() throws DatasourceFindException {
        ThingLogic logic = ThingLogic.getThingLogic();
        // Register for event
        logic.registerOnEvent(42, 1, new PropertyChangeEvent());
        Stack<EventInstance> eventI = logic.getRegisteredEvents(42);
        // There should be no eventinstances
        assertEquals(0, eventI.size());
        PropertyChangeEventInstance<String> eventInstance = new PropertyChangeEventInstance<String>(new Property<String>("", ""), 1, new Timestamp(0));
        eventInstance.setThingId(1);
        // submitting a instance
        logic.submitEvent(eventInstance);
        eventI = logic.getRegisteredEvents(42);
        // now there should be one instance
        eventI = logic.getRegisteredEvents(42);
        assertEquals(0, eventI.size());
        // deristiger
        logic.deRegisterOnEvent(42, 1, new PropertyChangeEvent());
        PropertyChangeEventInstance<String> eventInstance2 = new PropertyChangeEventInstance<String>(new Property<String>("", ""), 1, new Timestamp(0));
        eventInstance2.setThingId(1);
        logic.submitEvent(eventInstance2);
        eventI = logic.getRegisteredEvents(42);
        assertEquals(0, eventI.size());
    }

}
