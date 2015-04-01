package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * Simple Simulator for Things that should be simulated only with the functionality of changing their {@link Property}.
 *
 */
public class SimpleSimulator extends Simulator<Thing> {

    /**
     * Constructor for the {@link SimpleSimulator}.
     * 
     * @param thing
     *            thing that should be simulated
     * @param scheduler
     *            scheduler to be used
     */
    public SimpleSimulator(Thing thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        // Nothing to do here, the SimpleSimulator only simulates property changes. This changes are automatically handled through the
        // framework
    }

}
