package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Simulates a electricity meter that measures the current and overall consumption.
 *
 */
public class WaterMeterSimulator extends MeterSimulator {

    /**
     * Constructor.
     * @param thing .
     * @param scheduler .
     */
    public WaterMeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected double getAverageConsumption() {
        return 0;
    }

    @Override
    protected double getConsumptionVariation() {
        return 0;
    }

}
