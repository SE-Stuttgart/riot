package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Simulates a gas meter that measures the current and overall consumption.
 *
 */
public class GasMeterSimulator extends MeterSimulator {

    private static final double AVERAGE_CONSUMPTION = 20;
    private static final double VARIATION = 0;

    /**
     * Constructor.
     * @param thing .
     * @param scheduler .
     */
    public GasMeterSimulator(Meter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected double getAverageConsumption() {
        // TODO if heating is finished this value could be dynamicly changed according to the heating state (per event register)
        return AVERAGE_CONSUMPTION;
    }

    @Override
    protected double getConsumptionVariation() {
        return VARIATION;
    }

}
