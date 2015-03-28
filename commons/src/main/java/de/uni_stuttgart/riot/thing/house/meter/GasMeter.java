package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A gas meter delivers its parameters to the gas supplier and the house owner. 
 *
 */
public class GasMeter extends Meter {

    /**
     * Constructor.
     * 
     * @param behavior
     *            behavior to be used
     */
    public GasMeter(ThingBehavior behavior) {
        super(behavior);
    }
    
    /**
     * Getter for the {@link Property} of the current gas consumption in m^3.
     * 
     * @return the property
     */
    public Property<Double> getCurrentGasConsumtionProperty() {
        return this.currentConsumption;
    }

    /**
     * Getter for the current gas consumption in m^3.
     * 
     * @return current gas consumption in m^3.
     */
    public Double getCurrentGasConsumtion() {
        return this.currentConsumption.get();
    }
    
    /**
     * Getter for the {@link Property} of the overall gas consumption in m^3.
     * 
     * @return the property
     */
    public Property<Double> getOverallGasConsumtionProperty() {
        return this.overallConsumption;
    }

    /**
     * Getter for the overall gas consumption in m^3.
     * 
     * @return overall gas consumption in m^3.
     */
    public Double getOverallGasConsumtion() {
        return this.overallConsumption.get();
    }

}
