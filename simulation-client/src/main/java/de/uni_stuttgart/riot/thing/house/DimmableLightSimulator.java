package de.uni_stuttgart.riot.thing.house;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.house.light.AdjustDimmLevel;
import de.uni_stuttgart.riot.thing.house.light.DimmableLight;

/**
 * Simulation for dimmable light.
 *
 */
public class DimmableLightSimulator extends Simulator<DimmableLight>{

    /**
     * Constructor.
     * @param thing .
     * @param scheduler .
     */
    public DimmableLightSimulator(DimmableLight thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (action == this.getThing().getAdjustDimm()) {
            AdjustDimmLevel dL = (AdjustDimmLevel)actionInstance;
            this.adjustDimmLevel(dL.getLevel());
        }
    }

    /**
     * Adjusts the dimm level.
     * @param level
     */
    private void adjustDimmLevel(double level) {
        changePropertyValue(this.getThing().getDimmLevelProperty(), level);
    }

}
