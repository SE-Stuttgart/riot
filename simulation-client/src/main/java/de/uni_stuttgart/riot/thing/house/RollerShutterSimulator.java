package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.house.roller_shutter.AdjustShutterPostion;
import de.uni_stuttgart.riot.thing.house.roller_shutter.RollerShutter;

/**
 * 
 * Simulates a roller shutter.
 *
 */
public class RollerShutterSimulator extends Simulator<RollerShutter> {

    private static final int STEPTIME = 50;
    private static final int STEPCOUNT_MULT = 300;

    /**
     * Constructor for the {@link RollerShutterSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public RollerShutterSimulator(RollerShutter thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (action == this.getThing().getAdjustAction()) {
            AdjustShutterPostion aI = (AdjustShutterPostion) actionInstance;
            this.adjustPosition(aI.getPosition());
        }
    }

    /**
     * Adjusts the position
     * 
     * @param position
     *            target position
     */
    private void adjustPosition(double position) {
        int stepCount = (int) (Math.abs(this.getThing().getLevel() - position) * STEPCOUNT_MULT);
        if (stepCount != 0) {
            this.linearChange(this.getThing().getLevelProperty(), position, STEPTIME, stepCount);
        }
    }
}
