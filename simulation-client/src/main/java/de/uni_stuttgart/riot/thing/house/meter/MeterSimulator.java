package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Base class for all Meter simulators. Implements a simulation of the current consumption based on average and variation.
 */
public abstract class MeterSimulator extends Simulator<Meter> {

    /**
     * The period in that the consumption is updated.
     */
    public static final long CONSUMPTION_UPDATE_PERIOD = 500;

    /**
     * The number of milliseconds in an hour.
     */
    public static final long HOUR_IN_MILLI = 3600000;

    /**
     * Handle for the consumption task.
     */
    private ScheduledFuture<?> consumptionFuture;

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
    }

    @Override
    public void startSimulation() {
        super.startSimulation();
        consumptionFuture = this.scheduleAtFixedRate(this::simulateConsumption, 0, CONSUMPTION_UPDATE_PERIOD);
    }

    @Override
    public void stopSimulation() {
        super.stopSimulation();
        if (consumptionFuture != null) {
            consumptionFuture.cancel(true);
            consumptionFuture = null;
        }
    }

    private void simulateConsumption() {
        if (MeterSimulator.this.getThing().isBlocked()) {
            return;
        }
        double change = (getThing().getCurrentConsumptionProperty().get() / HOUR_IN_MILLI) * CONSUMPTION_UPDATE_PERIOD;
        double newOverallConsumption = getThing().getOverallConsumption() + change;
        changePropertyValue(getThing().getOverallConsumptionProperty(), newOverallConsumption);
        double newCurrentConsumption = getAverageConsumption() + (random(-getConsumptionVariation(), getConsumptionVariation()));
        changePropertyValue(getThing().getCurrentConsumptionProperty(), newCurrentConsumption);
    }

    @Override
    protected synchronized <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

    /**
     * Getter for the current average consumption.
     * 
     * @return the average.
     */
    public abstract double getAverageConsumption();

    /**
     * Getter for the current variation in the consumption.
     * 
     * @return the variation
     */
    public abstract double getConsumptionVariation();

}
