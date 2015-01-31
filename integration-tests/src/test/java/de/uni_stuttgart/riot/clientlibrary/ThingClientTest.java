package de.uni_stuttgart.riot.clientlibrary;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;

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
        thingClient.registerThing(new RemoteThing("Coffee Machine", 1));
        RemoteThing newThing = thingClient.getThing(4);
        assertEquals("Coffee Machine", newThing.getName());
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
    public void updateThingTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        thingClient.updateThing(2, new RemoteThing("Coffee Machine", 1));
        RemoteThing updated = thingClient.getThing(2);
        assertEquals("Coffee Machine", updated.getName());
    }

    @Test
    public void removeThingTest() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClient = this.getLogedInThingClient();
        thingClient.deregisterThing(2);
        Collection<RemoteThing> things = thingClient.getThings();
        assertEquals(2, things.size());
    }
}
