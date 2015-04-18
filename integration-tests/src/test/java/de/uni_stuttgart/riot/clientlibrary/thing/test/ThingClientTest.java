package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.test.ResetHelper;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingMetainfo;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.rest.UserThingShare;
import de.uni_stuttgart.riot.thing.test.TestActionInstance;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.test.TestThingBehavior;
import de.uni_stuttgart.riot.thing.ui.UIHint;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql" })
public class ThingClientTest extends BaseClientTest {

    @Before
    public void clearThingLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResetHelper.resetThingLogic();
        ResetHelper.resetRuleLogic();
        ResetHelper.resetServerReferenceResolver();
    }

    public ThingClient getLoggedInThingClient() {
        return new ThingClient(getLoggedInConnector());
    }

    public ThingClient getR2D2ThingClient() throws UnauthenticatedException, IOException, RequestException {
        ServerConnector secondConnector = produceNewServerConnector();
        secondConnector.login("R2D2", "R2D2PW");
        return new ThingClient(secondConnector);
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
        assertThat(thingClient.getOnlineState(thing.getId()), is(OnlineState.STATUS_ONLINE));

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
    public void changeThingMetainfo() throws IOException, RequestException, NotFoundException {

        // Create a new test thing.
        ThingClient thingClient = this.getLoggedInThingClient();
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();
        TestThing thing = (TestThing) thingClient.registerNewThing("FirstName", TestThing.class.getName(), null, mockBehaviorFactory);
        long id = thing.getId();

        // Get the metainfo of the created thing.
        ThingMetainfo metainfo = thingClient.getMetainfo(id);
        assertThat(metainfo.getName(), is("FirstName"));
        assertThat(metainfo.getOwnerId(), is(1L)); // Yoda is logged in.
        assertThat(metainfo.getParentId(), is(nullValue()));

        // Change the name through the metainfo.
        metainfo.setName("NewName");
        metainfo = thingClient.setMetainfo(id, metainfo);
        assertThat(metainfo.getName(), is("NewName"));
        metainfo = thingClient.getMetainfo(id);
        assertThat(metainfo.getName(), is("NewName"));

        // Change the parent Thing to test thing 1.
        metainfo.setParentId(1L);
        metainfo = thingClient.setMetainfo(id, metainfo);
        assertThat(metainfo.getName(), is("NewName"));
        assertThat(metainfo.getOwnerId(), is(1L));

        // Changing a Thing owned by another user should fail.
        try {
            ThingClient thingClientR2 = getR2D2ThingClient();
            thingClientR2.setMetainfo(id, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
        }

        // Setting parent to thing without READ-privileges should fail.
        try {
            metainfo.setParentId(2L);
            thingClient.setMetainfo(id, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
        }

        // Unregister it.
        thingClient.unregisterThing(id);
    }

    @Test
    public void unallowedMetainfoChanges() throws IOException, RequestException, NotFoundException {

        ThingClient thingClient = this.getLoggedInThingClient();
        ThingMetainfo metainfo = thingClient.getMetainfo(1L);

        // Setting name to null should fail.
        try {
            metainfo.setName(null);
            thingClient.setMetainfo(1L, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
            metainfo.setName("NewName");
        }

        // Setting owner to null should fail.
        try {
            metainfo.setOwnerId(null);
            thingClient.setMetainfo(1L, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
            metainfo.setOwnerId(1L);
        }

        // Updating a non-existent thing should fail.
        try {
            thingClient.setMetainfo(100L, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
        }

        // Getting a non-existent thing should fail.
        try {
            thingClient.getMetainfo(100L);
            fail();
        } catch (NotFoundException e) {
            // Expected
        }

        // Setting non-existent parent should fail.
        try {
            metainfo.setParentId(100L);
            thingClient.setMetainfo(1L, metainfo);
            fail();
        } catch (RequestException e) {
            // Expected
        }

        // Setting parent to self should fail.
        try {
            metainfo.setParentId(1L);
            thingClient.setMetainfo(1L, metainfo);
            fail();
        } catch (RequestException e) {
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

        // Unregister.
        thingClient.unregisterFromEvent(thing.getId(), request);

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
        ThingClient thingClientR2D2 = getR2D2ThingClient();

        // try to read the thing with the id 1, should fail
        try {
            thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
            fail();
        } catch (RequestException e) {
            // exception expected, because R2D2 has not the right to read thing/1
        }

        // R2D2 only has access to Thing 2
        Collection<ThingInformation> foundThings = thingClientR2D2.findThings(TestThing.class.getName(), EnumSet.of(ThingPermission.READ));
        List<Long> foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(1));
        assertThat(foundThingIDs, containsInAnyOrder(2L));

        // allow R2D2 to read the thing with the id 1
        thingClientYoda.share(1, 2, EnumSet.of(ThingPermission.READ));
        Thing thing = thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
        assertThat(thing.getName(), is("My Test Thing"));

        // check the permissions reported by the server
        Map<Long, ThingShare> permissions = thingClientYoda.getThingShares(1).stream().collect(Collectors.toMap(ThingShare::getUserId, Function.identity()));
        assertThat(permissions.get(1L).getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));
        assertThat(permissions.get(2L).getPermissions(), equalTo(EnumSet.of(ThingPermission.READ)));
        Map<Long, UserThingShare> permissions2 = thingClientYoda.getUserThingShares(1).stream().collect(Collectors.toMap(e -> e.getUser().getId(), Function.identity()));
        assertThat(permissions2.get(1L).getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));
        assertThat(permissions2.get(1L).getUser().getUsername(), is("Yoda"));
        assertThat(permissions2.get(2L).getPermissions(), equalTo(EnumSet.of(ThingPermission.READ)));
        assertThat(permissions2.get(2L).getUser().getUsername(), is("R2D2"));

        // Should now be able to find the thing 1, too
        foundThings = thingClientR2D2.findThings(TestThing.class.getName(), EnumSet.of(ThingPermission.READ));
        foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(2));
        assertThat(foundThingIDs, containsInAnyOrder(1L, 2L));

        // Not with too greedy permissions, though
        foundThings = thingClientR2D2.findThings(TestThing.class.getName(), EnumSet.of(ThingPermission.READ, ThingPermission.EXECUTE));
        foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(1));
        assertThat(foundThingIDs, containsInAnyOrder(2L));

        // Unshare again
        thingClientYoda.unshare(1, 2);
        try {
            thingClientR2D2.getExistingThing(1L, TestThingBehavior.getMockFactory());
            fail();
        } catch (RequestException e) {
            // exception expected, because R2D2 has not the right to read thing/1
        }
    }

    @Test
    public void findByTypeTest() throws IOException, RequestException {
        ThingClient thingClient = this.getLoggedInThingClient();
        // Yoda has full access to Thing 1, nothing else.
        Collection<ThingInformation> foundThings = thingClient.findThings(TestThing.class.getName(), EnumSet.of(ThingPermission.READ));
        List<Long> foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(1));
        assertThat(foundThingIDs, containsInAnyOrder(1L));

        foundThings = thingClient.findThings(TestThing.class.getName(), EnumSet.allOf(ThingPermission.class));
        foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(1));
        assertThat(foundThingIDs, containsInAnyOrder(1L));

        foundThings = thingClient.findThings(null, EnumSet.of(ThingPermission.READ));
        foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, hasSize(1));
        assertThat(foundThingIDs, containsInAnyOrder(1L));

        foundThings = thingClient.findThings(CoffeeMachine.class.getName(), EnumSet.of(ThingPermission.READ));
        foundThingIDs = foundThings.stream().map(ThingInformation::getId).collect(Collectors.toList());
        assertThat(foundThingIDs, is(empty()));
    }

    @Test
    public void getThingInformationsTest() throws RequestException, IOException, NotFoundException {

        ThingClient thingClient = this.getLoggedInThingClient();

        // Get single ThingInformation with metainfo only.
        ThingInformation info = thingClient.getThingInformation(1, EnumSet.of(ThingInformation.Field.METAINFO));
        assertThat(info.getId(), is(1L));
        assertThat(info.getType(), is(TestThing.class.getName()));
        assertThat(info.getDescription(), is(nullValue()));
        assertThat(info.getState(), is(nullValue()));
        assertThat(info.getShares(), is(nullValue()));
        assertThat(info.getLastConnection(), is(nullValue()));
        assertThat(info.getMetainfo().getName(), is("My Test Thing"));
        assertThat(info.getMetainfo().getOwnerId(), is(0L));
        assertThat(info.getMetainfo().getParentId(), is(nullValue()));

        // Get ThingInformation wiht all fields.
        info = thingClient.getThingInformation(1, EnumSet.allOf(ThingInformation.Field.class));
        assertThat(info.getId(), is(1L));
        assertThat(info.getType(), is(TestThing.class.getName()));
        assertThat(info.getDescription(), not(nullValue()));
        assertThat(info.getState(), not(nullValue()));
        assertThat(info.getShares(), not(nullValue()));
        assertThat(info.getMetainfo(), not(nullValue()));

        // Get all ThingInformations.
        ThingInformation info2 = thingClient.getThingInformations(EnumSet.allOf(ThingInformation.Field.class)).iterator().next();
        assertThat(info2.getId(), is(1L));
        assertThat(info2.getType(), is(TestThing.class.getName()));
        assertThat(info2.getDescription(), not(nullValue()));
        assertThat(info2.getState(), not(nullValue()));
        assertThat(info2.getShares(), not(nullValue()));
        assertThat(info2.getMetainfo(), not(nullValue()));

        // Getting a non-existent thing should fail.
        try {
            thingClient.getThingInformation(100, EnumSet.allOf(ThingInformation.Field.class));
            fail();
        } catch (NotFoundException e) {
            // Expected
        }

        // Get ThingDescription.
        ThingDescription description = thingClient.getDescription(1);
        assertThat(description.getThingId(), is(1L));
        assertThat(description.getPropertyByName("int").getUiHint(), instanceOf(UIHint.IntegralSlider.class));

        // Getting a non-existent thing should fail.
        try {
            thingClient.getDescription(100);
            fail();
        } catch (NotFoundException e) {
            // Expected
        }
    }

    @Test
    public void thingStateTest() throws RequestException, IOException, NotFoundException {

        // Create a new test thing.
        ThingClient thingClient = this.getLoggedInThingClient();
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = TestThingBehavior.getMockFactory();
        TestThing thing = (TestThing) thingClient.registerNewThing("FirstName", TestThing.class.getName(), null, mockBehaviorFactory);

        // Change a property value through the ThingState.
        ThingState state = thingClient.getThingState(thing.getId());
        assertThat(state.get("int"), is(42));
        state.set("int", 43);
        state = thingClient.setThingState(thing.getId(), state);
        assertThat(state.get("int"), is(43));
        state = thingClient.getThingState(thing.getId());
        assertThat(state.get("int"), is(43));

        // Updating a non-existent thing state should fail.
        try {
            thingClient.setThingState(100L, state);
            fail();
        } catch (RequestException e) {
            // Expected
        }

        // Getting a non-existent thing state should fail.
        try {
            thingClient.getThingState(100L);
            fail();
        } catch (NotFoundException e) {
            // Expected
        }

        // Unregister it.
        thingClient.unregisterThing(thing.getId());

    }

}
