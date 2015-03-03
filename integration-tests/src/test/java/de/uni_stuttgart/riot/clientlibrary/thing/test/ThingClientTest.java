package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.BaseInstanceDescription;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.thing.rest.RegisterRequest;
import de.uni_stuttgart.riot.thing.test.TestActionInstance;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.test.TestThingBehavior;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ThingClientTest extends ShiroEnabledTest {

    @Before
    public void clearThingLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = ThingLogic.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    public ThingClient getLoggedInThingClient() throws ClientProtocolException, RequestException, IOException {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        return new ThingClient(loginClient);
    }

    @Test
    public void registerNewThing() throws ClientProtocolException, RequestException, IOException {

        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();

        // Build the initial state
        ThingState initialState = new ThingState();
        initialState.set("int", 43);
        initialState.set("long", 4343L);
        initialState.set("readonlyString", "SomethingElse");

        // Register the thing.
        long timeBefore = System.currentTimeMillis();
        ThingClient thingClient = this.getLoggedInThingClient();
        TestThing thing = (TestThing) thingClient.registerNewThing("TestThing", TestThing.class.getName(), initialState, mockBehaviorFactory);
        assertThat(thing, instanceOf(TestThing.class));
        assertThat(thing.getId(), notNullValue());
        assertThat(thing.getId(), is(not(0)));
        assertThat(thing.getInt(), is(43));
        assertThat(thing.getLong(), is(4343L));
        assertThat(thing.getReadonlyString(), is("SomethingElse"));

        // Check its online status.
        assertThat(thingClient.getLastOnline(thing.getId()).getTime(), greaterThanOrEqualTo(timeBefore));

        // Unregister it.
        thingClient.unregisterThing(thing.getId());

        // Should be gone now.
        try {
            thingClient.getLastOnline(thing.getId());
            fail();
        } catch (RequestException e) {
            // Expected
        }

    }

    @Test
    public void registerExistingThing() throws RequestException, ClientProtocolException, IOException {

        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();

        // Get the existing thing.
        ThingClient thingClient = this.getLoggedInThingClient();
        Thing thing = thingClient.getExistingThing(1, mockBehaviorFactory);
        assertThat(thing, instanceOf(TestThing.class));
        assertThat(thing.getId(), is(1L));
        assertThat(thing.getName(), is("My Test Thing"));

    }

    @Test
    public void registerEventTest() throws ClientProtocolException, RequestException, IOException {

        // Build the thing that will receive the event.
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();
        ThingClient thingClient = this.getLoggedInThingClient();
        Thing thing = thingClient.registerNewThing("TestThing", TestThing.class.getName(), null, mockBehaviorFactory);

        // Get the existing thing that will fire the event.
        TestThing otherThing = (TestThing) thingClient.getExistingThing(1, mockBehaviorFactory);

        // Register for the event.
        RegisterRequest request = new RegisterRequest(1, "parameterizedEvent");
        thingClient.registerToEvent(thing.getId(), request);

        // So far, there should be nothing.
        assertThat(thingClient.getUpdates(thing.getId()).getOccuredEvents(), is(empty()));

        // Fake the event.
        thingClient.notifyEvent(new TestEventInstance(otherThing.getParameterizedEvent(), 4242));

        // Now it should be there.
        Collection<EventInstance> events = thingClient.getUpdates(thing.getId()).getOccuredEvents();
        assertThat(events, hasSize(1));
        TestEventInstance reportedInstance = (TestEventInstance) events.iterator().next();
        assertThat(reportedInstance.getParameter(), is(4242));
        assertThat(reportedInstance.getThingId(), is(otherThing.getId()));

        // Tidy up.
        thingClient.unregisterThing(thing.getId());

    }

    @Test
    public void fireActionTest() throws ClientProtocolException, RequestException, IOException {

        // Build the thing that the action will be executed on.
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();
        ThingClient thingClient = this.getLoggedInThingClient();
        Thing thing = thingClient.registerNewThing("TestThing", TestThing.class.getName(), null, mockBehaviorFactory);

        // So far, there should be nothing.
        assertThat(thingClient.getUpdates(thing.getId()).getOutstandingActions(), is(empty()));

        // Execute the action.
        thingClient.submitAction(new TestActionInstance(thing.getAction("parameterizedAction"), 4242));

        // Now it should be there.
        Collection<ActionInstance> actions = thingClient.getUpdates(thing.getId()).getOutstandingActions();
        assertThat(actions, hasSize(1));
        TestActionInstance reportedInstance = (TestActionInstance) actions.iterator().next();
        assertThat(reportedInstance.getParameter(), is(4242));
        assertThat(reportedInstance.getThingId(), is(thing.getId()));

        // Tidy up.
        thingClient.unregisterThing(thing.getId());

    }

    @Test
    public void retrieveThingDescription() throws ClientProtocolException, RequestException, IOException {

        ThingDescription description = getLoggedInThingClient().getThingDescription(1);
        assertThat(description.getType(), isClass(TestThing.class));

        // Check the events.
        assertThat(description.getEvents(), hasSize(2));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getInstanceType() == EventInstance.class, is(true));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getParameters().isEmpty(), is(true));
        BaseInstanceDescription parEventInstance = description.getEventByName("parameterizedEvent").getInstanceDescription();
        assertThat(parEventInstance.getInstanceType(), isClass(TestEventInstance.class));
        assertThat(parEventInstance.getParameters().size(), is(1));
        assertThat(parEventInstance.getParameters().get("parameter"), isClass(Integer.TYPE));

        // Check the actions.
        assertThat(description.getActions(), hasSize(2));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getInstanceType() == ActionInstance.class, is(true));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getParameters().isEmpty(), is(true));
        BaseInstanceDescription parActionInstance = description.getActionByName("parameterizedAction").getInstanceDescription();
        assertThat(parActionInstance.getInstanceType(), isClass(TestActionInstance.class));
        assertThat(parActionInstance.getParameters().size(), is(1));
        assertThat(parActionInstance.getParameters().get("parameter"), isClass(Integer.TYPE));

        // Check the properties.
        assertThat(description.getProperties(), hasSize(3));
        assertThat(description.getPropertyByName("int").getValueType(), isClass(Integer.class));
        assertThat(description.getPropertyByName("long").getValueType(), isClass(Long.class));
        assertThat(description.getPropertyByName("readonlyString").getValueType(), isClass(String.class));

    }

    private static Matcher<Class<?>> isClass(Class<?> clazz) {
        return CoreMatchers.<Class<?>> sameInstance(clazz);
    }

}
