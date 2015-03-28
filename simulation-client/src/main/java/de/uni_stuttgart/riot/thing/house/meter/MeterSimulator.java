package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Subclass for all Meter simulators. Implements a simulation of the current consumption based on average and variation.
 */
public abstract class MeterSimulator extends Simulator<Meter> {

    /**
     * Constructor.
     * 
     * @param thing
     *            thing to be simulated
     * @param scheduler
     *            .
     * @param average
     *            average consumption that should be base of the simulation.
     * @param variation
     *            variation of from the average consumption.
     */
    public MeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler, double average, double variation) {
        super(thing, scheduler);
    }

    @Override
    protected synchronized <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

}
