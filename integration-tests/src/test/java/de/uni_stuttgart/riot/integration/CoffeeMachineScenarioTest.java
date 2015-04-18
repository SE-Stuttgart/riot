package de.uni_stuttgart.riot.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.rule.house.AutoRefillRule;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachineSimulator;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CupSize;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfBeans;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfWater;

/**
 * Test scenario for the coffee machine.
 * 
 * @author Philipp Keck
 */
public class CoffeeMachineScenarioTest extends BaseScenarioTest {

    @Test
    public void scenarioCoffeeMachine() throws RequestException, IOException, NotFoundException, InterruptedException {

        // Set up the coffee machine.
        TestBehavior<CoffeeMachine> behavior = simulateThing(CoffeeMachine.class, CoffeeMachineSimulator.class, "Test machine");
        CoffeeMachine machine = behavior.getThing();

        // Turn it on (should work immediately, because it has an executing behavior.
        machine.setPowerSwitch(true);
        machine.setCupSize(CupSize.DOUBLE);
        assertThat(machine.isPowerOn(), is(true));

        // Set up the auto-refill rule for the machine.
        newRule(AutoRefillRule.class, "Refill my machine") //
                .param("machine", machine) //
                .param("refillBeansAmount", 33) //
                .param("delay", 0) //
                .create();

        // Make the water almost empty, have it refilled automatically.
        behavior.fetchUpdates();
        behavior.changePropertyValue(machine.getWaterTankProperty(), CupSize.SMALL.getWaterConsumption() - 1);
        behavior.executeEvent(new OutOfWater(machine.getOutOfWaterEvent(), machine.getWaterTank()));
        Thread.sleep(50);
        behavior.fetchUpdates();
        assertThat(machine.getWaterTank(), is(CoffeeMachine.WATER_TANK_SIZE));

        // Make the beans almost empty, have it refilled automatically.
        behavior.changePropertyValue(machine.getBeanTankProperty(), 20);
        behavior.executeEvent(new OutOfBeans(machine.getOutOfBeansEvent(), machine.getBeanTank()));
        Thread.sleep(50);
        behavior.fetchUpdates();
        assertThat(machine.getBeanTank(), is(20 + 33));

    }

}
