package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.thing.test.TestActionInstance;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.test.TestThingBehavior;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql" })
public class ThingClientTest extends BaseClientTest {

    @Before
    public void clearThingLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = ThingLogic.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    public ThingClient getLoggedInThingClient() {
        return new ThingClient(getLoggedInConnector());
    }

    @Test
    public void registerNewThing() throws RequestException, IOException, NotFoundException {

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
        } catch (NotFoundException e) {
            // Expected
        }

    }

    @Test
    public void registerExistingThing() throws RequestException, IOException, NotFoundException {

        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();

        // Get the existing thing.
        ThingClient thingClient = this.getLoggedInThingClient();
        Thing thing = thingClient.getExistingThing(1, mockBehaviorFactory);
        assertThat(thing, instanceOf(TestThing.class));
        assertThat(thing.getId(), is(1L));
        assertThat(thing.getName(), is("My Test Thing"));

    }

    @Test
    public void registerEventTest() throws RequestException, IOException, NotFoundException {

        // Build the thing that will receive the event.
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();
        ThingClient thingClient = this.getLoggedInThingClient();
        Thing thing = thingClient.registerNewThing("TestThing", TestThing.class.getName(), null, mockBehaviorFactory);

        // Get the existing thing that will fire the event.
        TestThing otherThing = (TestThing) thingClient.getExistingThing(1, mockBehaviorFactory);

        // Register for the event.
        RegisterEventRequest request = new RegisterEventRequest(1, "parameterizedEvent");
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
    public void fireActionTest() throws RequestException, IOException, NotFoundException {

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
    public void shareTest() throws Exception {
        ThingClient thingClientYoda = this.getLoggedInThingClient();

        ServerConnector secondConnector = produceNewServerConnector();
        secondConnector.login("R2D2", "R2D2PW");
        ThingClient thingClientR2D2 = new ThingClient(secondConnector);

        // try to read the thing with the id 1, should fail
        try {
            thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
            fail();
        } catch (RequestException e) {
            // exception expected, because R2D2 has not the right to read thing/1
        }

        // allow R2D2 to read the thing with the id 1
        thingClientYoda.share(1, 2, EnumSet.of(ThingPermission.READ));
        Thing thing = thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
        assertThat(thing.getName(), is("My Test Thing"));

        // check the permissions reported by the server
        Map<Long, ThingShare> permissions = thingClientYoda.getThingShares(1).stream().collect(Collectors.toMap(ThingShare::getUserId, Function.identity()));
        assertThat(permissions.get(1L).getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));
        assertThat(permissions.get(2L).getPermissions(), equalTo(EnumSet.of(ThingPermission.READ)));

        // and unshare again
        thingClientYoda.unshare(1, 2);
        try {
            thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
            fail();
        } catch (RequestException e) {
            // exception expected, because R2D2 has not the right to read thing/1
        }
    }

}
