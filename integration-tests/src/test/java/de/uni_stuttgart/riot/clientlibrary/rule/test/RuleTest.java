package de.uni_stuttgart.riot.clientlibrary.rule.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.client.RuleClient;
import de.uni_stuttgart.riot.rule.house.AutoRefillRule;
import de.uni_stuttgart.riot.server.test.ResetHelper;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.client.TypedExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CupSize;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfBeans;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfWater;
import de.uni_stuttgart.riot.thing.house.coffeemachine.RefillBeans;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_rules.sql" })
public class RuleTest extends BaseClientTest {

    private ThingClient getLoggedInThingClient() throws RequestException, IOException {
        return new ThingClient(getLoggedInConnector());
    }

    private RuleClient getLoggedInRuleClient() throws RequestException, IOException {
        return new RuleClient(getLoggedInConnector());
    }

    @Before
    public void clearRuleLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResetHelper.resetThingLogic();
        ResetHelper.resetRuleLogic();
        ResetHelper.resetServerReferenceResolver();
    }

    @Test
    public void scenarioCoffeeMachine() throws RequestException, IOException, NotFoundException, InterruptedException {

        // Set up the coffee machine.
        CoffeeMachineBehavior behavior = CoffeeMachineBehavior.create(getLoggedInThingClient(), "Test machine");
        CoffeeMachine machine = behavior.getThing();

        // Turn it on (should work immediately, because it has an executing behavior.
        machine.setPowerSwitch(true);
        machine.setCupSize(CupSize.DOUBLE);
        assertThat(machine.isPowerOn(), is(true));

        // Set up the auto-refill rule for the machine.
        RuleClient ruleClient = getLoggedInRuleClient();
        RuleConfiguration configuration = new RuleConfiguration(AutoRefillRule.class.getName());
        configuration.setName("Refill my machine");
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.set("machine", machine.getId());
        configuration.set("refillBeansAmount", 33);
        configuration.set("delay", 0);
        configuration = ruleClient.addNewRule(configuration);

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

        // Tidy up.
        ruleClient.deleteRule(configuration.getId());
        behavior.unregisterAndShutdown();
    }

    private static class CoffeeMachineBehavior extends TypedExecutingThingBehavior<CoffeeMachine> {

        public CoffeeMachineBehavior(ThingClient thingClient) {
            super(thingClient, CoffeeMachine.class);
        }

        @Override
        public <V> void changePropertyValue(Property<V> property, V newValue) {
            super.changePropertyValue(property, newValue);
        }

        @Override
        public <E extends EventInstance> void executeEvent(E eventInstance) {
            super.executeEvent(eventInstance);
        }

        @Override
        public void fetchUpdates() throws IOException, NotFoundException {
            super.fetchUpdates();
        }

        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
            if (action == getThing().getRefillWaterAction()) {
                changePropertyValue(getThing().getWaterTankProperty(), CoffeeMachine.WATER_TANK_SIZE);
            } else if (action == getThing().getRefillBeansAction()) {
                int refillAmount = ((RefillBeans) actionInstance).getAmount();
                int newValue = Math.min(CoffeeMachine.BEAN_TANK_SIZE, getThing().getBeanTank() + refillAmount);
                changePropertyValue(getThing().getBeanTankProperty(), newValue);
            } else if (action == getThing().getEmptyDripTrayAction()) {
                changePropertyValue(getThing().getDripTrayProperty(), 0.0);
            }
        }

        public static CoffeeMachineBehavior create(ThingClient client, String name) throws RequestException, IOException {
            @SuppressWarnings("unchecked")
            ThingBehaviorFactory<CoffeeMachineBehavior> behaviorFactory = mock(ThingBehaviorFactory.class);
            when(behaviorFactory.newBehavior(any())).thenReturn(new CoffeeMachineBehavior(client));
            return ExecutingThingBehavior.launchNewThing(CoffeeMachine.class, client, name, behaviorFactory);
        }

    }

}
