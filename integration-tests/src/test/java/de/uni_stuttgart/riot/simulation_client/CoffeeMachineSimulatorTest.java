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

import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachineSimulator;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CupSize;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfWater;

public class CoffeeMachineSimulatorTest {

    SimulatedThingBehavior behavior;
    CoffeeMachine coffeeMachine;
    CoffeeMachineSimulator simulator;

    @Before
    public void setupSimulator() {
        behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        coffeeMachine = new CoffeeMachine(behavior);
        coffeeMachine.setId(1L);
        simulator = SimulatorHelper.spySimulator(new CoffeeMachineSimulator(coffeeMachine, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();
    }

    @Test
    public void shouldConsumeWaterAndBeans() {
        coffeeMachine.setPowerSwitch(true);
        coffeeMachine.setCupSize(CupSize.MEDIUM);
        coffeeMachine.setBrewStrength(0.5);
        assertThat(coffeeMachine.isBusy(), is(false));
        assertThat(coffeeMachine.getWaterTank(), is(CoffeeMachine.WATER_TANK_SIZE));
        assertThat(coffeeMachine.getBeanTank(), is(CoffeeMachine.BEAN_TANK_SIZE));
        coffeeMachine.pressStart();
        assertThat(coffeeMachine.getWaterTank(), is(CoffeeMachine.WATER_TANK_SIZE - CupSize.MEDIUM.getWaterConsumption()));
        assertThat(coffeeMachine.getBeanTank(), is(CoffeeMachine.BEAN_TANK_SIZE - (int) Math.ceil(CupSize.MEDIUM.getBeanConsumption() * 0.5)));
        assertThat(coffeeMachine.isBusy(), is(false));
    }

    @Test
    public void shouldDoNothingWhenTurnedOff() {
        coffeeMachine.setPowerSwitch(false);
        coffeeMachine.pressStart();
        assertThat(coffeeMachine.getWaterTank(), is(CoffeeMachine.WATER_TANK_SIZE));
        assertThat(coffeeMachine.getBeanTank(), is(CoffeeMachine.BEAN_TANK_SIZE));
    }

    @Test
    public void shouldWarnOnEmptyTank() {
        @SuppressWarnings("unchecked")
        EventListener<OutOfWater> listener = mock(EventListener.class);
        coffeeMachine.setPowerSwitch(true);
        coffeeMachine.setCupSize(CupSize.DOUBLE);
        behavior.changePropertyValue(coffeeMachine.getWaterTankProperty(), CupSize.DOUBLE.getWaterConsumption() - 1);
        coffeeMachine.getOutOfWaterEvent().register(listener);
        coffeeMachine.pressStart();
        assertThat(coffeeMachine.getWaterTank(), is(CupSize.DOUBLE.getWaterConsumption() - 1));

        ArgumentCaptor<OutOfWater> eventInstance = ArgumentCaptor.forClass(OutOfWater.class);
        verify(listener).onFired(Mockito.same(coffeeMachine.getOutOfWaterEvent()), eventInstance.capture());
        assertThat(eventInstance.getValue().getRemainingWater(), is(coffeeMachine.getWaterTank()));
    }

    @Test
    public void shouldClean() {
        coffeeMachine.setPowerSwitch(true);
        assertThat(coffeeMachine.isBusy(), is(false));
        assertThat(coffeeMachine.getDripTray(), is(0.0));
        coffeeMachine.clean();
        assertThat(coffeeMachine.getDripTray(), is(100.0));
        assertThat(coffeeMachine.isBusy(), is(false));

        coffeeMachine.emptyDripTray();
        assertThat(coffeeMachine.getDripTray(), is(0.0));
    }

    @Test
    public void shouldRefill() {
        coffeeMachine.setPowerSwitch(false); // Can be off.
        behavior.changePropertyValue(coffeeMachine.getWaterTankProperty(), 0.0);
        behavior.changePropertyValue(coffeeMachine.getBeanTankProperty(), 0);

        coffeeMachine.refillWater();
        assertThat(coffeeMachine.getWaterTank(), is(CoffeeMachine.WATER_TANK_SIZE));

        coffeeMachine.refillBeans(20);
        assertThat(coffeeMachine.getBeanTank(), is(20));

        coffeeMachine.refillBeans(CoffeeMachine.BEAN_TANK_SIZE * 2);
        assertThat(coffeeMachine.getBeanTank(), is(CoffeeMachine.BEAN_TANK_SIZE));
    }

}
