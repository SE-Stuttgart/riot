package de.uni_stuttgart.riot.simulation_client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.house.RadiatorSimulator;
import de.uni_stuttgart.riot.thing.house.heating.Radiator;

public class RadiatorSimulatorTest {

    SimulatedThingBehavior behavior;
    Radiator radiator;
    RadiatorSimulator simulator;

    @Before
    public void setupSimulator() {
        behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        radiator = new Radiator(behavior);
        radiator.setId(1L);
        simulator = SimulatorHelper.spySimulator(new RadiatorSimulator(radiator, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();
    }

    @Test
    public void shouldDoHeating() throws ReflectiveOperationException {

        // Set up the radiator and listeners.
        radiator.setConfiguredTemp(24.0);
        behavior.changePropertyValue(radiator.getMeasuredTempProperty(), 20.0);

        @SuppressWarnings("unchecked")
        EventListener<EventInstance> onEventListener = mock(EventListener.class);
        radiator.getOnEvent().register(onEventListener);

        @SuppressWarnings("unchecked")
        EventListener<EventInstance> offEventListener = mock(EventListener.class);
        radiator.getOffEvent().register(offEventListener);

        // Change temperature to 22, should cool down to 19
        behavior.changePropertyValue(radiator.getMeasuredTempProperty(), 22.0);
        SimulatorHelper.invokeDeclaredMethod(simulator, "doHeating");
        verify(onEventListener, never()).onFired(Mockito.any(), Mockito.any());
        verify(offEventListener, never()).onFired(Mockito.any(), Mockito.any());
        assertThat(radiator.getMeasuredTemp(), is(19.0));

        // Change temperature to 20, should heat up to 24.
        behavior.changePropertyValue(radiator.getMeasuredTempProperty(), 20.0);
        SimulatorHelper.invokeDeclaredMethod(simulator, "doHeating");
        verify(onEventListener).onFired(Mockito.any(), Mockito.any());
        verify(offEventListener, never()).onFired(Mockito.any(), Mockito.any());
        assertThat(radiator.getMeasuredTemp(), is(24.0));

        // Run it again, should turn off and cool down to 19.
        SimulatorHelper.invokeDeclaredMethod(simulator, "doHeating");
        verify(offEventListener).onFired(Mockito.any(), Mockito.any());
        assertThat(radiator.getMeasuredTemp(), is(19.0));

    }

}
