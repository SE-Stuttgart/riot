package de.uni_stuttgart.riot.simulation_client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.junit.Test;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.house.meter.ElectricityMeter;
import de.uni_stuttgart.riot.thing.house.meter.ElectricityMeterSimulator;
import de.uni_stuttgart.riot.thing.house.meter.GasMeter;
import de.uni_stuttgart.riot.thing.house.meter.GasMeterSimulator;
import de.uni_stuttgart.riot.thing.house.meter.MeterSimulator;
import de.uni_stuttgart.riot.thing.house.meter.WaterMeter;
import de.uni_stuttgart.riot.thing.house.meter.WaterMeterSimulator;

public class MeterSimulatorTest {

    private static final double EPSILON = 0.001;

    @Test
    public void testMaximumConsumption() throws ReflectiveOperationException {

        // Setup a GasMeter.
        SimulatedThingBehavior behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        GasMeter meter = new GasMeter(behavior);
        meter.setId(1L);
        GasMeterSimulator simulator = SimulatorHelper.spySimulator(new GasMeterSimulator(meter, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();

        // Make the random method always return the maximum.
        double variation = simulator.getConsumptionVariation();
        when(simulator.random(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(variation);

        // Let it consume n times, the first time will consume 0.
        int n = 10;
        for (int i = 0; i < n; i++) {
            SimulatorHelper.invokeDeclaredMethod(simulator, "simulateConsumption");
        }
        double consumption = simulator.getAverageConsumption() + variation;
        assertThat(meter.getCurrentConsumption(), is(consumption));
        consumption = consumption / MeterSimulator.HOUR_IN_MILLI * MeterSimulator.CONSUMPTION_UPDATE_PERIOD;
        assertThat(meter.getOverallConsumption(), is(closeTo(consumption * (n - 1), EPSILON)));

    }

    @Test
    public void testMinimumConsumption() throws ReflectiveOperationException {

        // Setup a ElectricityMeter.
        SimulatedThingBehavior behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        ElectricityMeter meter = new ElectricityMeter(behavior);
        meter.setId(1L);
        ElectricityMeterSimulator simulator = SimulatorHelper.spySimulator(new ElectricityMeterSimulator(meter, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();

        // Make the random method always return the minimum.
        double variation = simulator.getConsumptionVariation();
        when(simulator.random(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(-variation);

        // Let it consume n times, the first time will consume 0.
        int n = 10;
        for (int i = 0; i < n; i++) {
            SimulatorHelper.invokeDeclaredMethod(simulator, "simulateConsumption");
        }
        double consumption = simulator.getAverageConsumption() - variation;
        assertThat(meter.getCurrentConsumption(), is(consumption));
        consumption = consumption / MeterSimulator.HOUR_IN_MILLI * MeterSimulator.CONSUMPTION_UPDATE_PERIOD;
        assertThat(meter.getOverallConsumption(), is(closeTo(consumption * (n - 1), EPSILON)));

    }

    @Test
    public void testRandomConsumption() throws ReflectiveOperationException {

        // Setup a WaterMeter.
        SimulatedThingBehavior behavior = new SimulatedThingBehavior(Mockito.mock(ThingClient.class));
        WaterMeter meter = new WaterMeter(behavior);
        meter.setId(1L);
        WaterMeterSimulator simulator = SimulatorHelper.spySimulator(new WaterMeterSimulator(meter, new ScheduledThreadPoolExecutor(1)));
        simulator.startSimulation();

        // Let it consume n times, the first time will consume 0.
        int n = 10;
        for (int i = 0; i < n; i++) {
            SimulatorHelper.invokeDeclaredMethod(simulator, "simulateConsumption");
        }
        double minConsumption = simulator.getAverageConsumption() - simulator.getConsumptionVariation();
        double maxConsumption = simulator.getAverageConsumption() + simulator.getConsumptionVariation();
        assertThat(meter.getCurrentConsumption(), is(greaterThanOrEqualTo(minConsumption)));
        assertThat(meter.getCurrentConsumption(), is(lessThanOrEqualTo(maxConsumption)));
        minConsumption = minConsumption / MeterSimulator.HOUR_IN_MILLI * MeterSimulator.CONSUMPTION_UPDATE_PERIOD;
        maxConsumption = maxConsumption / MeterSimulator.HOUR_IN_MILLI * MeterSimulator.CONSUMPTION_UPDATE_PERIOD;
        assertThat(meter.getOverallConsumption(), is(greaterThanOrEqualTo(minConsumption * (n - 1) - EPSILON)));
        assertThat(meter.getOverallConsumption(), is(lessThanOrEqualTo(maxConsumption * (n - 1) + EPSILON)));
    }

}
