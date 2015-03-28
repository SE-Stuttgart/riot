package de.uni_stuttgart.riot.thing.house.meter;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WaterMeterSimulator extends MeterSimulator {

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
