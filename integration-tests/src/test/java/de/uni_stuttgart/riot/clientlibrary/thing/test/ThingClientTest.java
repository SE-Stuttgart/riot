package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ThingClientTest extends ShiroEnabledTest {

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

    public ThingClient getLogedInThingClient() throws ClientProtocolException, RequestException, IOException {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        return new ThingClient(loginClient);
    }

    @Test
    public void addThingTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        RemoteThing thing = new RemoteThing("Coffee Machine", 1);
        thing.addProperty(new Property<Boolean>("State", false));
        thing.addAction(new PropertySetAction<Boolean>("State"));
        thing.addEvent(new PropertyChangeEvent<Boolean>("State"));
        thingClient.registerThing(thing);
        RemoteThing newThing = thingClient.getThing(4);
        assertEquals("Coffee Machine", newThing.getName());
        assertEquals(1, newThing.getActions().size());
        assertEquals(1, newThing.getEvents().size());
        assertEquals(1, newThing.getProperties().size());
    }

    @Test
    public void lastOnlineTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        assertEquals(new Timestamp(0), thingClient.getLastOnline(5));
        thingClient.getActionInstances(1);
        Timestamp tm = new Timestamp(System.currentTimeMillis() - 1000);
        assertEquals(true, tm.before(thingClient.getLastOnline(1)));
    }

    @Test
    public void getThingTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        RemoteThing thing = thingClient.getThing(1);
        assertEquals("Android", thing.getName());
    }

    @Test
    public void getThingsTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        Collection<RemoteThing> things = thingClient.getThings();
        assertEquals(3, things.size());
    }

    @Test
    public void removeThingTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        thingClient.deregisterThing(2);
        Collection<RemoteThing> things = thingClient.getThings();
        assertEquals(2, things.size());
    }
}
