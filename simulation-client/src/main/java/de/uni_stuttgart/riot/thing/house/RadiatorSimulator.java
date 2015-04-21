package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.house.heating.Radiator;

/**
 * Simulates a {@link Radiator}.
 */
public class RadiatorSimulator extends Simulator<Radiator> {

    private static final double DEFAULT_TEMP = 19.0;

    private static final long HEATING_TASK_PERIOD = 10000;

    private static final int HEATING_STEP_TIME = 2000;

    private static final long COOLING_STEP_TIME = 5000;

    private ScheduledFuture<?> heatingTaskFuture;

    /**
     * {@link ScheduledFuture} is stored to be able to interrupted the current heating process.
     */
    private ScheduledFuture<?> tempChangeFuture;

    /**
     * Constructor for the {@link RadiatorSimulator}.
     * 
     * @param thing
     *            thing to be simulated
     * @param scheduler
     *            scheduler to be used
     */
    public RadiatorSimulator(Radiator thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    public void startSimulation() {
        super.startSimulation();
        heatingTaskFuture = this.scheduleAtFixedRate(this::doHeating, 0, HEATING_TASK_PERIOD);
    }

    @Override
    public void stopSimulation() {
        super.stopSimulation();
        if (heatingTaskFuture != null) {
            heatingTaskFuture.cancel(true);
            heatingTaskFuture = null;
        }
        if (tempChangeFuture != null) {
            tempChangeFuture.cancel(true);
            tempChangeFuture = null;
        }
    }

    private void doHeating() {
        double currentTemp = getThing().getMeasuredTemp();
        double threshold = getThing().getThreshold();
        double configTemp = getThing().getConfiguredTemp();
        if (tempChangeFuture != null) {
            tempChangeFuture.cancel(false);
        }
        if (currentTemp < (configTemp - threshold)) {
            int stepCount = (int) Math.abs(currentTemp - configTemp);
            boolean previouslyOn = getThing().getHeating();
            changePropertyValue(getThing().getHeatingProperty(), true);
            tempChangeFuture = linearChange(getThing().getMeasuredTempProperty(), configTemp, HEATING_STEP_TIME, stepCount);
            if (!previouslyOn) {
                executeEvent(getThing().getOnEvent());
            }
        } else {
            int stepCount = (int) Math.abs(currentTemp - DEFAULT_TEMP);
            boolean previouslyOn = getThing().getHeating();
            changePropertyValue(getThing().getHeatingProperty(), false);
            tempChangeFuture = linearChange(getThing().getMeasuredTempProperty(), DEFAULT_TEMP, COOLING_STEP_TIME, stepCount);
            if (previouslyOn) {
                executeEvent(getThing().getOffEvent());
            }
        }
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

}
