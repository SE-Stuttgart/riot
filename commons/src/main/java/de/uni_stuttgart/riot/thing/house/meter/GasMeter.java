package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A gas meter delivers its parameters to the gas supplier and the house owner. 
 *
 */
public class GasMeter extends Meter {

    /**
     * Constructor for the {@link GasMeter}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public GasMeter(ThingBehavior behavior) {
        super(behavior);
    }
    
}
