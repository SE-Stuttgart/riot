package de.uni_stuttgart.riot.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.junit.After;
import org.junit.Before;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.client.RuleClient;
import de.uni_stuttgart.riot.server.test.ResetHelper;
import de.uni_stuttgart.riot.simulation_client.SimulatedThingBehavior;
import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.simulation_client.SimulatorHelper;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.SingleUseThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * Base class for tests that execute an entire scenario, involving Things and Rules.
 * 
 * @author Philipp Keck
 */
@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", //
        "/schema/schema_things.sql", "/testdata/testdata_things.sql", //
        "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_rules.sql" })
public abstract class BaseScenarioTest extends BaseClientTest {

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);
    private final List<Simulator<?>> activeSimulators = new ArrayList<>();
    private final List<TestBehavior<?>> registeredThings = new ArrayList<>();
    private final List<RuleConfiguration> registeredRules = new ArrayList<>();

    protected ThingClient getLoggedInThingClient() throws RequestException, IOException {
        return new ThingClient(getLoggedInConnector());
    }

    protected RuleClient getLoggedInRuleClient() throws RequestException, IOException {
        return new RuleClient(getLoggedInConnector());
    }

    @Before
    public void clearLogics() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResetHelper.resetThingLogic();
        ResetHelper.resetRuleLogic();
        ResetHelper.resetServerReferenceResolver();
    }

    /**
     * Creates a new simulated thing for the scenario. Remember to call {@link TestBehavior#fetchUpdates()} when properties of the Thing
     * might have been updated and to retrieve events for the Thing.
     * 
     * @param thingType
     *            The type of the Thing.
     * @param simulatorType
     *            The type of the simulator (may be <tt>null</tt> for no simulation). See {@link SimulatorHelper#spySimulator(Simulator)}.
     * @param name
     *            The name of the thing.
     * @return The simulated thing.
     * @throws IOException
     * @throws RequestException
     */
    protected <T extends Thing> TestBehavior<T> simulateThing(Class<T> thingType, Class<? extends Simulator<? super T>> simulatorType, String name) throws RequestException, IOException {
        ThingClient thingClient = getLoggedInThingClient();
        TestBehavior<T> behavior = new TestBehavior<T>(thingClient, thingType);
        ExecutingThingBehavior.launchNewThing(thingType, thingClient, name, new SingleUseThingBehaviorFactory<>(behavior));
        registeredThings.add(behavior);
        if (simulatorType != null) {
            Simulator<? super T> simulator;
            try {

                simulator = ConstructorUtils.getMatchingAccessibleConstructor(simulatorType, thingType, ScheduledThreadPoolExecutor.class).newInstance(behavior.getThing(), scheduler);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            simulator = SimulatorHelper.spySimulator(simulator);
            simulator.startSimulation();
            activeSimulators.add(simulator);
        }
        return behavior;
    }

    @After
    public void shutdownRulesSimulatorsThings() throws IOException, RequestException {
        RuleClient ruleClient = getLoggedInRuleClient();
        for (RuleConfiguration config : registeredRules) {
            ruleClient.deleteRule(config.getId());
        }

        for (Simulator<?> simulator : activeSimulators) {
            simulator.stopSimulation();
        }
        scheduler.shutdown();

        for (TestBehavior<?> behavior : registeredThings) {
            behavior.unregisterAndShutdown();
        }
    }

    /**
     * Calls {@link TestBehavior#fetchUpdates()} for all test things.
     */
    protected void fetchAllUpdates() {
        try {
            for (TestBehavior<?> behavior : registeredThings) {
                behavior.fetchUpdates();
            }
        } catch (IOException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A thing behavior for things that are simulated for the purpose of integration tests.
     * 
     * @author Philipp Keck
     *
     * @param <T>
     *            The type of the thing.
     */
    protected class TestBehavior<T extends Thing> extends SimulatedThingBehavior {

        private final Class<T> thingType;

        public TestBehavior(ThingClient client, Class<T> thingType) {
            super(client);
            this.thingType = thingType;
        }

        @Override
        protected synchronized void register(Thing newThing) {
            if (thingType != newThing.getClass()) {
                throw new IllegalArgumentException(getClass().getName() + " can only be used with things of type " + thingType.getName());
            }
            super.register(newThing);
        }

        @Override
        public T getThing() {
            return thingType.cast(super.getThing());
        }

        @Override
        protected void fetchUpdates() throws IOException, NotFoundException {
            super.fetchUpdates();
        }

        @Override
        protected <E extends EventInstance> void executeEvent(E eventInstance) {
            super.executeEvent(eventInstance);
        }

        protected void executeEvent(Event<EventInstance> event) {
            executeEvent(new EventInstance(event));
        }

        @Override
        protected <V> void changePropertyValue(Property<V> property, V newValue) {
            super.changePropertyValue(property, newValue);
        }

        protected long getId() {
            return getThing().getId();
        }

    }

    /**
     * Provides a fluent API for creating rules.
     * 
     * @param type
     *            The type of the new rule.
     * @param
     * @return
     */
    protected RuleCreator newRule(Class<? extends Rule> type, String name) {
        return new RuleCreator(type.getName(), name);
    }

    /**
     * Provides a fluent API for creating rules.
     * 
     * @author Philipp
     *
     */
    protected class RuleCreator {
        private final RuleConfiguration config;

        private RuleCreator(String type, String name) {
            config = new RuleConfiguration(type);
            config.setName(name);
            config.setStatus(RuleStatus.ACTIVE);
        }

        /**
         * Adds a configuration parameter for the new rule.
         * 
         * @param name
         *            The name of the parameter.
         * @param value
         *            The value of the parameter.
         * @return <tt>this</tt>.
         */
        public RuleCreator param(String name, Object value) {
            if (value instanceof Referenceable) {
                Long id = ((Referenceable<?>) value).getId();
                if (id == null || id < 1) {
                    throw new IllegalArgumentException(value + " does not have an ID yet");
                }
                config.set(name, id);
            } else if (value instanceof TestBehavior) {
                config.set(name, ((TestBehavior<?>) value).getId());
            } else {
                config.set(name, value);
            }
            return this;
        }

        /**
         * Creates the rule (will be tidied up at the end of the unit test automatically).
         * 
         * @return The rule configuration.
         * @throws IOException
         *             When a network error occured.
         * @throws RequestException
         *             When the rule could not be created.
         */
        public RuleConfiguration create() throws IOException, RequestException {
            RuleConfiguration addedConfig = getLoggedInRuleClient().addNewRule(config);
            registeredRules.add(addedConfig);
            return addedConfig;
        }
    }

}
