package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * 
 * Simulates a roller shutter.
 *
 */
public class RollerShutterSimulator extends Simulator<RollerShutter> {

    
    /**
     * Constructor.
     * @param thing .
     * @param scheduler .
     */
    public RollerShutterSimulator(RollerShutter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

}
