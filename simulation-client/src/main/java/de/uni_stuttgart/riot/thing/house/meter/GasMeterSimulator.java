package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Simulates a gas meter that measures the current and overall consumption.
 *
 */
public class GasMeterSimulator extends MeterSimulator {

    private static final double AVERAGE_CONSUMPTION = 20;
    private static final double VARIATION = 8;

    /**
     * Constructor for the {@link GasMeterSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public GasMeterSimulator(GasMeter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    public double getAverageConsumption() {
        // TODO if heating is finished this value could be dynamicly changed according to the heating state (per event register)
        return AVERAGE_CONSUMPTION;
    }

    @Override
    public double getConsumptionVariation() {
        return VARIATION;
    }

}
