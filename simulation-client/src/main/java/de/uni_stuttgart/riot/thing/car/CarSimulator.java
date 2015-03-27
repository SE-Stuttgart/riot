package de.uni_stuttgart.riot.thing.car;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Simulates the behavior of a car.
 */
public class CarSimulator extends Simulator<Car> {

    private static final double FUEL_CONSUMPTION = 0.01;
    private static final int HEATING_STEP_TIME = 5000;
    private static final int CONSUMPTION_STEP_TIME = 10;
    private static final Double CRITICAL_TANK_LEVEL = 5.0;
    private static final long TANK_CONRTOLL_PERIOD = 10000;

    /**
     * {@link ScheduledFuture} is stored to be able to interrupted the current heating process.
     */
    private ScheduledFuture<?> heatingFuture;

    /**
     * {@link ScheduledFuture} is stored to be able to interrupted the current engine process.
     */
    private ScheduledFuture<?> engineFuture;

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
        this.initTankLevelControll();
    }

    /**
     * Starts the control Task for the tank fill level. It checks the current tank fill level against the
     * {@link CarSimulator#CRITICAL_TANK_LEVEL} if the current level is less than the critical level a out of gasoline event is fired.
     */
    private void initTankLevelControll() {
        this.scheduleAtFixedRate(() -> {
            if (CarSimulator.this.getThing().getTankFillLevel() < CarSimulator.CRITICAL_TANK_LEVEL) {
                if (CarSimulator.this.getThing().getTankFillLevel() == 0.0) {
                    changePropertyValue(CarSimulator.this.getThing().getEngineProperty(), false);
                }
                CarSimulator.this.fireOutOfGasoline(CarSimulator.this.getThing().getTankFillLevel());
            }
        }, 0, TANK_CONRTOLL_PERIOD);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (action == getThing().getRefuelAction()) {
            doRefuel(actionInstance);
        } else if (action == getThing().getAirConditionAction()) {
            doAirCondition(actionInstance);
        } else if (action == getThing().getEngineAction()) {
            doEngineActionHandling(actionInstance);
        }
    }

    /**
     * Starts (if currently off) or Stops (if currently on) the heating.
     * 
     * @param actionInstance
     */
    private <A extends ActionInstance> void doAirCondition(A actionInstance) {
        int stepCount = (int) Math.abs(getThing().getInteriorTemperature() - getThing().getConfiguredTemperature());
        if (getThing().isAirConditionOn()) {
            if (heatingFuture != null) {
                heatingFuture.cancel(false);
            }
            changePropertyValue(getThing().getAirConditionProperty(), false);
            heatingFuture = linearChange(getThing().getInteriorTemperatureProperty(), Car.DEFAULT_TEMP, HEATING_STEP_TIME, stepCount);
        } else {
            if (heatingFuture != null) {
                heatingFuture.cancel(false);
            }
            changePropertyValue(getThing().getAirConditionProperty(), true);
            heatingFuture = linearChange(getThing().getInteriorTemperatureProperty(), getThing().getConfiguredTemperature(), HEATING_STEP_TIME, stepCount);
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
            this.fireTankOverflow();
        }
        changePropertyValue(getThing().getTankProperty(), newValue);
    }

    /**
     * Fires the tankOverflow event.
     */
    private void fireTankOverflow() {
        executeEvent(getThing().getTankOverflow());
    }

    /**
     * Starts (if currently off) or Stops (if currently on) the engine.
     * 
     * @param actionInstance
     */
    private <A extends ActionInstance> void doEngineActionHandling(A actionInstance) {
        int stepCount = (int) Math.abs(getThing().getTankFillLevel() / FUEL_CONSUMPTION);
        if (getThing().isEngineStarted()) {
            changePropertyValue(getThing().getEngineProperty(), false);
            if (engineFuture != null) {
                engineFuture.cancel(false);
            }
        } else {
            changePropertyValue(getThing().getEngineProperty(), true);
            engineFuture = linearChange(getThing().getTankProperty(), 0.0, CONSUMPTION_STEP_TIME, stepCount);
        }
    }

    /**
     * Fires the outOfGasoline Event
     * 
     * @param remainingGasoline
     *            the remaining amount of gasoline.
     */
    private void fireOutOfGasoline(Double remainingGasoline) {
        executeEvent(new OutOfGasoline(getThing().getOutOfGasoline(), remainingGasoline));
    }

}
