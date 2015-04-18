package de.uni_stuttgart.riot.simulation_client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.car.Car;
import de.uni_stuttgart.riot.thing.car.CarSimulator;
import de.uni_stuttgart.riot.thing.car.OutOfGasoline;
import de.uni_stuttgart.riot.thing.client.ThingClient;

public class CarSimulatorTest {

    SimulatedThingBehavior behavior;
    Car car;
    CarSimulator simulator;

    @Before
    public void setupSimulator() {
        behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        car = new Car(behavior);
        car.setId(1L);
        simulator = SimulatorHelper.spySimulator(new CarSimulator(car, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();
    }

    @Test
    public void shouldUseFuel() throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        EventListener<OutOfGasoline> listener = mock(EventListener.class);
        car.getOutOfGasoline().register(listener);

        assertThat(car.getTankFillLevel(), is(40.0));
        assertThat(car.isEngineStarted(), is(false));
        car.getEngineAction().fire(new ActionInstance(car.getEngineAction())); // Turn on.
        assertThat(car.isEngineStarted(), is(true));

        // Since time passes infinitely fast, the tank will be empty now.
        assertThat(car.getTankFillLevel(), is(0.0));
        SimulatorHelper.invokeDeclaredMethod(simulator, "checkTankLevel"); // Call scheduled task manually.
        assertThat(car.isEngineStarted(), is(false));

        ArgumentCaptor<OutOfGasoline> eventInstance = ArgumentCaptor.forClass(OutOfGasoline.class);
        verify(listener).onFired(Mockito.same(car.getOutOfGasoline()), eventInstance.capture());
        assertThat(eventInstance.getValue().getRemainingGasoline(), is(0.0));

        // Refuel it.
        car.refuel(25.0);
        assertThat(car.getTankFillLevel(), is(25.0));

        // Refuel too much.
        @SuppressWarnings("unchecked")
        EventListener<EventInstance> listener2 = mock(EventListener.class);
        car.getTankOverflow().register(listener2);
        car.refuel(80.0);
        verify(listener2).onFired(Mockito.same(car.getTankOverflow()), Mockito.any());
    }

    @Test
    public void shouldDoAirConditioning() {
        assertThat(car.isAirConditionOn(), is(false));
        assertThat(car.getInteriorTemperature(), is(18.0));
        car.setConfiguredTemperature(24.0);
        car.getAirConditionAction().fire(new ActionInstance(car.getAirConditionAction()));
        assertThat(car.isAirConditionOn(), is(true));
        assertThat(car.getInteriorTemperature(), is(24.0));
        car.getAirConditionAction().fire(new ActionInstance(car.getAirConditionAction()));
        assertThat(car.isAirConditionOn(), is(false));
        assertThat(car.getInteriorTemperature(), is(18.0));
    }

}
