package de.uni_stuttgart.riot.thing.car;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Simulates the behavior of a car.
 *
 */
public class CarSimulator extends Simulator<Car> {

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The car
     * @param scheduler
     *            The scheduler
     */
    public CarSimulator(Car thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (action == getThing().getRefuelAction()) {
            doRefuel(actionInstance);
        } else if (action == getThing().getHeatingAction()) {
            doHeating(actionInstance);
        }
    }

    private AtomicReference<ScheduledFuture<?>> future;

    private <A extends ActionInstance> void doHeating(A actionInstance) {
        int stepCount = (int) Math.abs(getThing().getInteriorTemperature() - getThing().getConfiguredTemperature());
        long stepTime = 5000;
        if (getThing().isHeatingOn()) {
            if (future != null)
                future.get().cancel(false);
            changePropertyValue(getThing().getHeatingProperty(), false);
            future = linearChange(getThing().getInteriorTemperatureProperty(), Car.DEFAULT_HEATING_TEMP, stepTime, stepCount);
        } else {
            if (future != null)
                future.get().cancel(false);
            changePropertyValue(getThing().getHeatingProperty(), true);
            future = linearChange(getThing().getInteriorTemperatureProperty(), getThing().getConfiguredTemperature(), stepTime, stepCount);
        }
    }

    /**
     * Refuels the car. If the amount of gasoline is grater than the space left in the tank a RuntimeException is thrown.
     * 
     * @param actionInstance
     *            the Refuel actioninstance
     */
    private <A extends ActionInstance> void doRefuel(A actionInstance) {
        double refuelAmount = ((Refuel) actionInstance).getAmount();
        double newValue = refuelAmount + getThing().getTankFillLevel();
        if (newValue > Car.TANK_MAX_FILL_LEVEL) {
            throw new RuntimeException("You just made a mess");
        }
        changePropertyValue(getThing().getTankProperty(), newValue);
    }

}
