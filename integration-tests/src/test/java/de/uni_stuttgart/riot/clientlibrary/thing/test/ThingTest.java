package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Stack;

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
import de.uni_stuttgart.riot.thing.commons.RegisterRequest;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEventInstance;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ThingTest extends ShiroEnabledTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new RiotApplication(true);
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
    public void scenario() throws ClientProtocolException, RequestException, IOException {
        ThingClient thingClientF = this.getLogedInThingClient();
        ThingClient thingClient = this.getLogedInThingClient();
        thingClient.getActionInstances(1);
        Frigde frigde = new Frigde(thingClientF, "Peter", 500);
        frigde.register();
        frigde.start();
        long frigdeId = frigde.getId();
        thingClient.registerOnEvent(new RegisterRequest(42, frigdeId, frigde.stateChangeEvent));
        thingClient.registerOnEvent(new RegisterRequest(42, frigdeId, frigde.tempChangeEvent));
        thingClient.submitActionInstance(frigde.setStateAction.createInstance(true, frigdeId));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        Stack<EventInstance> eventInstances = thingClient.getEventInstances(42);
        PropertyChangeEventInstance<Boolean> i = (PropertyChangeEventInstance<Boolean>) eventInstances.pop();
        assertEquals(true, i.getNewProperty().getValue());
        assertEquals("State", i.getNewProperty().getName());
        frigde.stop();
        thingClient.getActionInstances(1);
    }

}
