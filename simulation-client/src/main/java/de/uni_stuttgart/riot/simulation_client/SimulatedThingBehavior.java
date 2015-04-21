package de.uni_stuttgart.riot.simulation_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * The behavior for a thing that is being simulated. Note that this class is not designed for inheritance. To add simulation logic for a
 * specific kind of thing, use the {@link Simulator} interface.
 * 
 * @author Philipp Keck
 */
public class SimulatedThingBehavior extends ExecutingThingBehavior {

    /**
     * Each of these interceptors will be called and can cancel the action by returning false.
     */
    final List<Function<ActionInstance, Boolean>> actionInterceptors = new ArrayList<>();

    /**
     * The simulator that currently simulates the behavior of the thing. Note that this may be null, in which case the thing does not react
     * to actions, etc.
     */
    private Simulator<?> simulator;

    /**
     * Creates a new instance.
     * 
     * @param client
     *            The client to communicate through.
     */
    public SimulatedThingBehavior(ThingClient client) {
        super(client);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        for (Function<ActionInstance, Boolean> actionInterceptor : actionInterceptors) {
            if (!actionInterceptor.apply(actionInstance)) {
                return;
            }
        }
        if (simulator != null) {
            simulator.executeAction(action, actionInstance);
        }
    }

    /**
     * Starts the simulation with the given simulator.
     * 
     * @param newSimulator
     *            The new simulator.
     */
    synchronized void startSimulation(Simulator<?> newSimulator) {
        if (simulator != null) {
            throw new IllegalStateException("A simulator is already active for this thing!");
        }
        simulator = newSimulator;
    }

    /**
     * Stops the simulation with the given simulator.
     * 
     * @param currentSimulator
     *            The current simulator.
     */
    synchronized void stopSimulation(Simulator<?> currentSimulator) {
        if (simulator != currentSimulator) {
            throw new IllegalStateException("The given simulator is not the active one!");
        }
        simulator = null;
    }

    @Override
    public void shutdown() {
        actionInterceptors.clear();
        super.shutdown();
        if (simulator != null) {
            stopSimulation(simulator);
        }
    }

    // The methods below are mostly here to expose them in the local package so that Simulator can use them.

    @Override
    protected Map<String, Property<?>> getProperties() {
        return super.getProperties();
    }

    @Override
    protected Map<String, Event<?>> getEvents() {
        return super.getEvents();
    }

    @Override
    protected <V> void changePropertyValue(Property<V> property, V newValue) {
        super.changePropertyValue(property, newValue);
    }

    @Override
    protected <E extends EventInstance> void executeEvent(E eventInstance) {
        super.executeEvent(eventInstance);
    }

    @Override
    protected void fetchUpdates() throws IOException, NotFoundException {
        super.fetchUpdates();
    }

    @Override
    protected ThingClient getClient() {
        return super.getClient();
    }

    @Override
    protected <T extends Thing> T getOtherThing(long id, Class<T> expectedType) throws NotFoundException, IOException {
        return super.getOtherThing(id, expectedType);
    }

}
