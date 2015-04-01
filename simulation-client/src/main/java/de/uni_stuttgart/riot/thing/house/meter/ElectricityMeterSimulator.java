package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Simulates a electricity meter that measures the current and overall consumption.
 *
 */
public class ElectricityMeterSimulator extends MeterSimulator {

    private static final double VARIATION = 0;
    private static final double AVERAGE_CONSUMPTION = 0;

    /**
     * Constructor for the {@link ElectricityMeterSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public ElectricityMeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected double getAverageConsumption() {
        // TODO if light and so on is finished this value could be dynamicly changed according to the light state (per event register)
        return AVERAGE_CONSUMPTION;
    }

    @Override
    protected double getConsumptionVariation() {
        return VARIATION;
    }

}
