package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A water meter delivers its parameters to the water supplier and the house owner. 
 *
 */
public class WaterMeter extends Meter {

    /**
     * Constructor for the {@link WaterMeter}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public WaterMeter(ThingBehavior behavior) {
        super(behavior);
    }
    
}
