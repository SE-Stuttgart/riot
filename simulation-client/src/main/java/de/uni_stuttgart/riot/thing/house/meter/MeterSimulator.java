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
     * The period in that the consumption is updated
     */
    private static final long CONSUMPTION_UPDATE_PERIOD = 500;

    private static final long HOUR_IN_MILLI = 3600000;

    /**
     * Constructor.
     * 
     * @param thing
     *            thing to be simulated
     * @param scheduler
     *            .
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
                double change = (MeterSimulator.this.getThing().getCurrentConsumtionProperty().get() / HOUR_IN_MILLI) * CONSUMPTION_UPDATE_PERIOD;
                double newOverallConsumption = MeterSimulator.this.getThing().getOverallConsumtionProperty().get() + change;
                changePropertyValue(MeterSimulator.this.getThing().getOverallConsumtionProperty(), newOverallConsumption);
                double newCurrentConsumption = MeterSimulator.this.getAverageConsumption() + (random(-MeterSimulator.this.getConsumptionVariation(), MeterSimulator.this.getConsumptionVariation()));
                changePropertyValue(MeterSimulator.this.getThing().getCurrentConsumtionProperty(), newCurrentConsumption);
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
