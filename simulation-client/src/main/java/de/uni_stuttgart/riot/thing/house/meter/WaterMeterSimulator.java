package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Simulates a electricity meter that measures the current and overall consumption.
 *
 */
public class WaterMeterSimulator extends MeterSimulator {

    private static final int VARIATION = 5;
    private static final int AVERAGE_CONSUMPTION = 5;

    /**
     * Constructor for the {@link WaterMeterSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public WaterMeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    public double getAverageConsumption() {
        return AVERAGE_CONSUMPTION;
    }

    @Override
    public double getConsumptionVariation() {
        return VARIATION;
    }

}
