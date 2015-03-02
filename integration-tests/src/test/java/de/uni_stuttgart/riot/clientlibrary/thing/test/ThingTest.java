package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.thing.test.Fridge.OutOfFoodEventInstance;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.client.ThingNotFoundException;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ThingTest extends ShiroEnabledTest {

    private ThingClient getLoggedInThingClient() throws ClientProtocolException, RequestException, IOException {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        return new ThingClient(loginClient);
    }

    public void scenarioFridge() throws ClientProtocolException, RequestException, IOException, ThingNotFoundException {

        Fridge.FridgeBehavior fridgeBehavior = Fridge.create(getLoggedInThingClient(), "Peter");
        Fridge fridge = fridgeBehavior.getFridge();

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

        // Shutdown
        observerBehavior.unregisterAndShutdown();
        fridgeBehavior.unregisterAndShutdown();

    }

    @Test
    public void scenarioExistingTestThing() throws ClientProtocolException, RequestException, IOException {
        // Login as the existing test thing with ID 1.
        ThingClient thingClient = getLoggedInThingClient();
        TestExecutingThingBehavior behavior = ExecutingThingBehavior.launchExistingThing(TestThing.class, thingClient, 1, TestExecutingThingBehavior.getMockFactory(thingClient));
        behavior.unregisterAndShutdown();
    }

}
