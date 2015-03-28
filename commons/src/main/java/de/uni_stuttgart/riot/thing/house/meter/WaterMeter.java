package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A water meter delivers its parameters to the water supplier and the house owner. 
 *
 */
public class WaterMeter extends Meter {

    /**
     * Constructor.
     * 
     * @param behavior
     *            behavior to be used
     */
    public WaterMeter(ThingBehavior behavior) {
        super(behavior);
    }
    
    /**
     * Getter for the {@link Property} of the current water consumption in liter.
     * 
     * @return the property
     */
    public Property<Double> getCurrentWaterConsumtionProperty() {
        return this.currentConsumption;
    }

    /**
     * Getter for the current water consumption in liter.
     * 
     * @return current water consumption in liter.
     */
    public Double getCurrentWaterConsumtion() {
        return this.currentConsumption.get();
    }
    
    /**
     * Getter for the {@link Property} of the overall water consumption in liter.
     * 
     * @return the property
     */
    public Property<Double> getOverallWaterConsumtionProperty() {
        return this.overallConsumption;
    }

    /**
     * Getter for the overall water consumption in liter.
     * 
     * @return overall water consumption in liter.
     */
    public Double getOverallWaterConsumtion() {
        return this.overallConsumption.get();
    }

}
