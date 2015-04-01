package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A smart meter is a electricity meter that is able to deliver its parameters to the electricity distributor and the house owner.
 *
 */
public class ElectricityMeter extends Meter {

    /**
     * Constructor for the {@link ElectricityMeter}.
     * 
     * @param behavior
     *            the behavior to be used
     */
    public ElectricityMeter(ThingBehavior behavior) {
        super(behavior);
    }

}
