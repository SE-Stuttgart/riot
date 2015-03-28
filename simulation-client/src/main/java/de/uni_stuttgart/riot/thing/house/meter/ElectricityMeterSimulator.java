package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ElectricityMeterSimulator extends MeterSimulator {

    private static final double VARIATION = 0;
    private static final double AVERAGE_CONSUMPTION = 0;

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
