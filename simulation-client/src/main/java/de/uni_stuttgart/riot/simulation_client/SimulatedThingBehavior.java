package de.uni_stuttgart.riot.simulation_client;

import java.util.Map;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
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
    protected <V> void changePropertyValue(Property<V> property, V newValue) {
        super.changePropertyValue(property, newValue);
    }

    @Override
    protected <E extends EventInstance> void executeEvent(E eventInstance) {
        super.executeEvent(eventInstance);
    }

    @Override
    protected void fetchUpdates() throws RequestException {
        super.fetchUpdates();
    }

}
