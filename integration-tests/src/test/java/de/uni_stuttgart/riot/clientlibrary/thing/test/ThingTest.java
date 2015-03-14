package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.clientlibrary.thing.test.Fridge.FiredDeliveryGuyEventInstance;
import de.uni_stuttgart.riot.clientlibrary.thing.test.Fridge.OutOfFoodEventInstance;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql" })
public class ThingTest extends BaseClientTest {

    private ThingClient getLoggedInThingClient() throws RequestException, IOException {
        return new ThingClient(getLoggedInConnector());
    }

    @Before
    public void clearThingLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = ThingLogic.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void scenarioFridge() throws RequestException, IOException, NotFoundException {

        Fridge.FridgeBehavior fridgeBehavior = Fridge.create(getLoggedInThingClient(), "Peter");
        Fridge fridge = fridgeBehavior.getThing();

        Observer.ObserverBehavior observerBehavior = Observer.create(getLoggedInThingClient(), "Olli");

        // We let the observer observe the fridge.
        // This will create a mirrored version of the fridge that is owned by the observer.
        // This mirrored version is synced with our actual fridge instance over the server.
        Fridge mirroredFridge = observerBehavior.observe(fridge.getId(), Fridge.class);

        // The mirror should reflect its original.
        assertThat(mirroredFridge.getState(), is(fridge.getState()));
        assertThat(mirroredFridge.getTemp(), is(fridge.getTemp()));

        // Now we simulate a temperature change and its propagation.
        fridgeBehavior.simulateTemperatureChange(-10);
        assertThat(fridge.getTemp(), is(-10));
        observerBehavior.updateObserved(mirroredFridge);
        assertThat(mirroredFridge.getTemp(), is(-10));

        // And once again. This time we fetch the updates by monitoring the thing.
        observerBehavior.startMonitoring(mirroredFridge);
        fridgeBehavior.simulateTemperatureChange(8);
        assertThat(fridge.getTemp(), is(8));
        observerBehavior.fetchUpdates();
        assertThat(mirroredFridge.getTemp(), is(8));

        // Now we fire a state change action.
        assertThat(fridge.getState(), is(false));
        assertThat(mirroredFridge.getState(), is(false));
        mirroredFridge.setState(true);
        fridgeBehavior.fetchUpdates();
        assertThat(fridge.getState(), is(true));
        assertThat(mirroredFridge.getState(), is(false));

        // The mirror needs another update to receive the new state.
        observerBehavior.fetchUpdates();
        assertThat(fridge.getState(), is(true));
        assertThat(mirroredFridge.getState(), is(true));

        // Eat all the bananas and check that the "out of bananas" event is raised.
        @SuppressWarnings("unchecked")
        EventListener<OutOfFoodEventInstance> listener = mock(EventListener.class);
        mirroredFridge.getOutOfFoodEvent().register(listener);
        mirroredFridge.eatEverythingOf("banana");
        verify(listener, never()).onFired(any(), any());
        fridgeBehavior.fetchUpdates();
        observerBehavior.fetchUpdates();

        ArgumentCaptor<OutOfFoodEventInstance> eventCaptor = ArgumentCaptor.forClass(OutOfFoodEventInstance.class);
        verify(listener).onFired(Mockito.same(mirroredFridge.getOutOfFoodEvent()), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getFood(), is("banana"));

        // Stop the monitoring and ensure that the property changes no longer arrive.
        assertThat(fridge.getTemp(), is(8));
        assertThat(mirroredFridge.getTemp(), is(8));
        observerBehavior.stopMonitoring(mirroredFridge);
        fridgeBehavior.simulateTemperatureChange(11);
        assertThat(fridge.getTemp(), is(11));
        observerBehavior.fetchUpdates();
        assertThat(mirroredFridge.getTemp(), is(8)); // Still the old value!

        // Shutdown
        observerBehavior.unregisterAndShutdown();
        fridgeBehavior.unregisterAndShutdown();

    }

    @Test
    public void scenarioDeliveryGuy() throws RequestException, IOException, NotFoundException, ResolveReferenceException {

        // As in the previous scenario, we have a fridge and an observer.
        Fridge.FridgeBehavior fridgeBehavior = Fridge.create(getLoggedInThingClient(), "Peter");
        Fridge fridge = fridgeBehavior.getThing();
        Observer.ObserverBehavior observerBehavior = Observer.create(getLoggedInThingClient(), "Olli");
        Fridge mirroredFridge = observerBehavior.observe(fridge.getId(), Fridge.class);

        // Since we will hire and fire delivery guys (which fill the fridge with new food), we need to reference those. We use the regular
        // RIOT users as possible employees, so we need to resolve User instances.
        UsermanagementClient umClient = new UsermanagementClient(getLoggedInConnector());
        fridgeBehavior.getDelegatingResolver().addResolver(User.class, umClient);
        observerBehavior.getDelegatingResolver().addResolver(User.class, umClient);

        // Retrieve test users.
        User yoda = umClient.getUser(1);
        User vader = umClient.getUser(3);

        // Register an event listener, so we see who got fired.
        @SuppressWarnings("unchecked")
        EventListener<FiredDeliveryGuyEventInstance> listener = mock(EventListener.class);
        mirroredFridge.getFiredDeliveryGuyEvent().register(listener);
        observerBehavior.startMonitoring(mirroredFridge);

        // Now we make Yoda the delivery guy, this is executed directly at the fridge client.
        assertThat(fridge.getDeliveryGuy(), is(nullValue()));
        assertThat(fridge.getDeliveryGuyId(), is(nullValue()));
        fridge.hireDeliveryGuy(yoda);
        assertThat(fridge.getDeliveryGuy(), is(yoda));
        assertThat(fridge.getDeliveryGuyId(), is(yoda.getId()));

        // This should not have fired anyone since there was no employee at the beginning.
        assertThat(mirroredFridge.getDeliveryGuy(), is(nullValue()));
        assertThat(mirroredFridge.getDeliveryGuyId(), is(nullValue()));
        observerBehavior.fetchUpdates();
        assertThat(mirroredFridge.getDeliveryGuy(), is(yoda));
        assertThat(mirroredFridge.getDeliveryGuyId(), is(yoda.getId()));
        verify(listener, never()).onFired(any(), any());

        // Now we fire Yoda by employing Vader. We request this from the observer's side.
        mirroredFridge.hireDeliveryGuy(vader);
        assertThat(mirroredFridge.getDeliveryGuy(), is(yoda));
        assertThat(mirroredFridge.getDeliveryGuyId(), is(yoda.getId()));

        // As usual, we need to propagate this to the executing client until it gets executed.
        fridgeBehavior.fetchUpdates();
        assertThat(fridge.getDeliveryGuy(), is(vader));
        assertThat(fridge.getDeliveryGuyId(), is(3L));

        // And we need to propagate it back to see the event being fired.
        assertThat(mirroredFridge.getDeliveryGuy(), is(yoda));
        assertThat(mirroredFridge.getDeliveryGuyId(), is(yoda.getId()));
        verify(listener, never()).onFired(any(), any());
        observerBehavior.fetchUpdates();
        assertThat(mirroredFridge.getDeliveryGuy(), is(vader));
        assertThat(mirroredFridge.getDeliveryGuyId(), is(vader.getId()));

        // Check that we really fired Yoda.
        ArgumentCaptor<FiredDeliveryGuyEventInstance> eventCaptor = ArgumentCaptor.forClass(FiredDeliveryGuyEventInstance.class);
        verify(listener, times(1)).onFired(Mockito.same(mirroredFridge.getFiredDeliveryGuyEvent()), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getPoorGuy().getId(), is(yoda.getId()));

        // Shutdown
        observerBehavior.unregisterAndShutdown();
        fridgeBehavior.unregisterAndShutdown();

    }

    @Test
    public void scenarioExistingTestThing() throws RequestException, IOException, NotFoundException {
        // Login as the existing test thing with ID 1.
        ThingClient thingClient = getLoggedInThingClient();
        TestExecutingThingBehavior behavior = ExecutingThingBehavior.launchExistingThing(TestThing.class, thingClient, 1, TestExecutingThingBehavior.getMockFactory(thingClient));
        behavior.unregisterAndShutdown();
    }

}
