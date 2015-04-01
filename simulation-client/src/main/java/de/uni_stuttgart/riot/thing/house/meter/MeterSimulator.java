package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Base class for all Meter simulators. Implements a simulation of the current consumption based on average and variation.
 */
public abstract class MeterSimulator extends Simulator<Meter> {

    /**
     * The period in that the consumption is updated
     */
    private static final long CONSUMPTION_UPDATE_PERIOD = 500;

    private static final long HOUR_IN_MILLI = 3600000;

    /**
     * Constructor for the {@link MeterSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public MeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
        this.scheduleConsumptionTask();
    }

    /**
     * Schedules the cosumption task that updates the current and overall consumption based on the average consumption and variation.
     */
    private void scheduleConsumptionTask() {
        this.scheduleAtFixedRate(() -> {
            if (!MeterSimulator.this.getThing().isBlocked()) {
                double change = (getThing().getCurrentConsumptionProperty().get() / HOUR_IN_MILLI) * CONSUMPTION_UPDATE_PERIOD;
                double newOverallConsumption = getThing().getOverallConsumption() + change;
                changePropertyValue(getThing().getOverallConsumptionProperty(), newOverallConsumption);
                double newCurrentConsumption = MeterSimulator.this.getAverageConsumption() + (random(-getConsumptionVariation(), getConsumptionVariation()));
                changePropertyValue(getThing().getCurrentConsumptionProperty(), newCurrentConsumption);
            }
        }, 0, CONSUMPTION_UPDATE_PERIOD);
    }

    @Override
    protected synchronized <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

    /**
     * Getter for the current average consumption.
     * 
     * @return the average.
     */
    protected abstract double getAverageConsumption();

    /**
     * Getter for the current variation in the consumption.
     * 
     * @return the variation
     */
    protected abstract double getConsumptionVariation();

}
