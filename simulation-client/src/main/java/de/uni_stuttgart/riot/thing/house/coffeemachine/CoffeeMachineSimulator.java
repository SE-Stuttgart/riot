package de.uni_stuttgart.riot.thing.house.coffeemachine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

// CHECKSTYLE: MagicNumber OFF
/**
 * Simulates the behavior of a coffee machine.
 * 
 * 
 * @author Philipp Keck
 */
public class CoffeeMachineSimulator extends Simulator<CoffeeMachine> {

    private static final long CLEANING_DURATION = 5000;
    private static final long CLEANING_WATER_CONSUMPTION = 100;
    private static final long COFFEE_MAKING_DURATION = 10000;

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The coffee machine.
     * @param scheduler
     *            The scheduler.
     */
    public CoffeeMachineSimulator(CoffeeMachine thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    private void fireOutOfWater() {
        executeEvent(new OutOfWater(getThing().getOutOfWaterEvent(), getThing().getWaterTank()));
    }

    private void fireOutOfBeans() {
        executeEvent(new OutOfBeans(getThing().getOutOfBeansEvent(), getThing().getBeanTank()));
    }

    @Override
    protected synchronized <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (action == getThing().getRefillWaterAction()) {
            changePropertyValue(getThing().getWaterTankProperty(), CoffeeMachine.WATER_TANK_SIZE);

        } else if (action == getThing().getRefillBeansAction()) {
            int refillAmount = ((RefillBeans) actionInstance).getAmount();
            int newValue = Math.min(CoffeeMachine.BEAN_TANK_SIZE, getThing().getBeanTank() + refillAmount);
            changePropertyValue(getThing().getBeanTankProperty(), newValue);

        } else if (action == getThing().getEmptyDripTrayAction()) {
            changePropertyValue(getThing().getDripTrayProperty(), 0.0);

        } else if (action == getThing().getCleanAction()) {
            cleanPressed();

        } else if (action == getThing().getPressStartAction()) {
            startPressed();
        }

    }

    /**
     * Cleans the machine, if it is not already busy. Cleaning just means that the machine is busy for a while and consumes some water, if
     * it is there.
     */
    private synchronized void cleanPressed() {
        if (getThing().isBusy() || !getThing().isPowerOn()) {
            // Ignore if busy.
            return;
        } else if (getThing().getWaterTank() < CLEANING_WATER_CONSUMPTION) {
            // Too little water left, inform and cancel.
            fireOutOfWater();
            return;
        }

        // Start cleaning.
        changePropertyValue(getThing().getBusyProperty(), true);

        // In the middle two quarters of the time, we consume water and produce waste water.
        int steps = 5;
        long stepDuration = CLEANING_DURATION / 2 / steps;
        delay(CLEANING_DURATION / 4, () -> {
            linearChange(getThing().getWaterTankProperty(), Math.max(0, getThing().getWaterTank() - CLEANING_WATER_CONSUMPTION), stepDuration, steps);
            linearChange(getThing().getDripTrayProperty(), getThing().getDripTray() + CLEANING_WATER_CONSUMPTION, stepDuration, steps);
        });

        // After the total time, we're done.
        delay(CLEANING_DURATION, () -> {
            changePropertyValue(getThing().getBusyProperty(), false);
        });
    }

    /**
     * Executes the regular coffee making.
     */
    private synchronized void startPressed() {
        if (getThing().isBusy() || !getThing().isPowerOn()) {
            // Ignore if busy.
            return;
        }

        // Check if we have enough water.
        CupSize size = getThing().getCupSize();
        double waterConsumption = size.getWaterConsumption();
        if (getThing().getWaterTank() < waterConsumption) {
            fireOutOfWater();
            return;
        }

        // Check if we have enough beans.
        int beanConsumption = (int) Math.ceil(size.getBeanConsumption() * getThing().getBrewStrength());
        if (getThing().getBeanTank() < beanConsumption) {
            fireOutOfBeans();
            return;
        }

        // Start the coffee making.
        changePropertyValue(getThing().getBusyProperty(), true);

        // Immediately use the water for brewing, resoluted in 10 steps, total is half of the coffee making time.
        int waterSteps = 10;
        long waterStepDuration = COFFEE_MAKING_DURATION / 2 / waterSteps;
        linearChange(getThing().getWaterTankProperty(), getThing().getWaterTank() - waterConsumption, waterStepDuration, waterSteps);

        // After half of the time, quickly use the beans.
        delay(COFFEE_MAKING_DURATION / 2, () -> {
            int beanSteps = 5;
            long beanStepDuration = COFFEE_MAKING_DURATION / 4 / beanSteps;
            linearChange(getThing().getBeanTankProperty(), getThing().getBeanTank() - beanConsumption, beanStepDuration, beanSteps);
        });

        // At the end of the time, the coffee is finished
        delay(COFFEE_MAKING_DURATION, () -> {
            // Notify the user about his coffee.
                executeEvent(getThing().getCoffeeFinishedEvent());

                if (randomDecision(0.5)) {
                    // Assume that a random amount was spilled and went into the drip tray.
                double spilledWater = random(0, waterConsumption);
                changePropertyValue(getThing().getDripTrayProperty(), getThing().getDripTray() + spilledWater);
            }

            // We're done.
            changePropertyValue(getThing().getBusyProperty(), false);
        });

    }

}
