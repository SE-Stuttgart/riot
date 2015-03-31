package de.uni_stuttgart.riot.thing.factory.robot;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;

/**
 * Simulates the behavior of a factory robot.
 */
public class RobotSimulator extends Simulator<Robot> {

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The factory robot.
     * @param scheduler
     *            The scheduler.
     */
    public RobotSimulator(Robot thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {

        if (getThing().getRobotStatus() != RobotStatus.WAITING || !getThing().isPowerOn()) {
            // Ignore if not waiting.
            return;
        }
        if (action == getThing().getPressRefillMaterialTankAction()) { // refill material tank

            changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.ON_THE_WAY);
            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {
                // Processing is done.
                // fire events on the factory machine
                    executeEvent(new EventInstance(getThing().getMaterialTankIsFilled()));

                    changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.WAITING);
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });

        } else if (action == getThing().getPressEmptyProcessedPiecesTankAction()) { // empty processed pieces

            changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.ON_THE_WAY);
            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {
                // Processing is done.
                // fire events on the factory machine
                    executeEvent(new EventInstance(getThing().getProcessedPiecesTankIsEmptyEvent()));

                    changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.WAITING);
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });
        }
    }
}
