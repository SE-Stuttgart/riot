package de.uni_stuttgart.riot.simulation_client;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * The behavior for a thing that is being simulated.
 * 
 * @author Philipp Keck
 */
class SimulatedThingBehavior extends ExecutingThingBehavior {

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
        // TODO Implement this
        throw new UnsupportedOperationException();
    }

}