package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.house.light.Light;

/**
 * Simulatior for light.
 *
 */
public class LightSimulator extends Simulator<Light>{

    /**
     * Constructor.
     * @param thing . 
     * @param scheduler .
     */
    public LightSimulator(Light thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }
}
