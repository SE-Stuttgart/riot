package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A smart meter is a electricity meter that is able to deliver its parameters to the electricity distributor and the house owner.
 *
 */
public class ElectricityMeter extends Meter {

    /**
     * Constructor.
     * 
     * @param behavior
     *            the behavior to use
     */
    public ElectricityMeter(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the {@link Property} of the current electricity consumption in kw/h.
     * 
     * @return the property
     */
    public Property<Double> getCurrentElectricityConsumtionProperty() {
        return this.currentConsumption;
    }

    /**
     * Getter for the current electricity consumption in kw/h.
     * 
     * @return current electricity consumption in kw/h.
     */
    public Double getCurrentElectricityConsumtion() {
        return this.currentConsumption.get();
    }
    
    /**
     * Getter for the {@link Property} of the overall electricity consumption in kw/h.
     * 
     * @return the property
     */
    public Property<Double> getOverallElectricityConsumtionProperty() {
        return this.overallConsumption;
    }

    /**
     * Getter for the overall electricity consumption in kw/h.
     * 
     * @return overall electricity consumption in kw/h.
     */
    public Double getOverallElectricityConsumtion() {
        return this.overallConsumption.get();
    }

}
