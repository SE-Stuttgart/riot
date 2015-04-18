package de.uni_stuttgart.riot.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.rule.GoodMorningRule;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.house.AlarmClock;
import de.uni_stuttgart.riot.thing.house.AlarmClockSimulator;
import de.uni_stuttgart.riot.thing.house.RollerShutterSimulator;
import de.uni_stuttgart.riot.thing.house.SimpleSimulator;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachineSimulator;
import de.uni_stuttgart.riot.thing.house.light.DimmableLight;
import de.uni_stuttgart.riot.thing.house.roller_shutter.RollerShutter;

/**
 * Tests the {@link GoodMorningRule} and the associated things.
 * 
 * @author Philipp Keck
 */
public class GoodMorningScenarioTest extends BaseScenarioTest {

    @Test
    public void scenarioGoodMorning() throws RequestException, IOException {

        // Set up things.
        TestBehavior<AlarmClock> alarmClock = simulateThing(AlarmClock.class, AlarmClockSimulator.class, "My alarm clock");
        TestBehavior<RollerShutter> rollerShutter = simulateThing(RollerShutter.class, RollerShutterSimulator.class, "My roller shutter");
        TestBehavior<DimmableLight> dimmableLight = simulateThing(DimmableLight.class, SimpleSimulator.class, "My dimmable light");
        TestBehavior<CoffeeMachine> coffeeMachine = simulateThing(CoffeeMachine.class, CoffeeMachineSimulator.class, "My coffee machine");

        // Set up the rule.
        newRule(GoodMorningRule.class, "My wake up rule") //
                .param("alarmClock", alarmClock) //
                .param("rollerShutter", rollerShutter) //
                .param("dimmableLight", dimmableLight) //
                .param("coffeeMachine", coffeeMachine) //
                .create();

        // Set everything to night.
        rollerShutter.getThing().adjustShutter(100.0);
        dimmableLight.getThing().setDimLevel(0.0);
        dimmableLight.getThing().setOn(false);
        coffeeMachine.getThing().setPowerSwitch(false);
        fetchAllUpdates();
        assertThat(rollerShutter.getThing().getLevel(), is(100.0));
        assertThat(dimmableLight.getThing().getDimLevel(), is(0.0));
        assertThat(dimmableLight.getThing().isOn(), is(false));
        assertThat(coffeeMachine.getThing().isPowerOn(), is(false));

        // Let the alarm clock ring.
        alarmClock.executeEvent(alarmClock.getThing().getAlarmEvent());

        // Check that everything woke up.
        @SuppressWarnings("unchecked")
        EventListener<EventInstance> coffeeListener = mock(EventListener.class);
        coffeeMachine.getThing().getCoffeeFinishedEvent().register(coffeeListener);
        fetchAllUpdates();
        assertThat(rollerShutter.getThing().getLevel(), is(0.0));
        assertThat(dimmableLight.getThing().getDimLevel(), is(0.5));
        assertThat(dimmableLight.getThing().isOn(), is(true));
        assertThat(coffeeMachine.getThing().isPowerOn(), is(true));
        verify(coffeeListener).onFired(Mockito.any(), Mockito.any());

    }

}
