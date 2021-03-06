package de.uni_stuttgart.riot.thing.house.meter;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Superclass for all meters such as GasMeter, WaterMeter,...
 *
 */
public abstract class Meter extends Thing {

    private static final Double CURRENT_CONSUMPTION = 0.0;

    private static final Double OVERALL_CONSUMPTION = 0.0;
    
    /**
     * Holds the value of the current consumption eg. kw/h for electricity.
     */
    private final Property<Double> currentConsumption = newProperty("currentConsumption", Double.class, CURRENT_CONSUMPTION, UIHint.editNumber());

    /**
     * Holds the overall amount of consumption eg. kw/h for electricity.
     */
    private final Property<Double> overallConsumption = newProperty("overallConsumption", Double.class, OVERALL_CONSUMPTION, UIHint.editNumber());
   
    /**
     * Shows if the meter has blocked the connection (eg. Water supply is blocked), eg. because of a open account.
     */
    private final Property<Boolean> blocked = newProperty("blocked", Boolean.class, false, UIHint.toggleButton());
    
   
    /**
     * Constructor for {@link Meter}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public Meter(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Returns the blocked {@link Property}.
     * 
     * @return the blocked
     */
    public Property<Boolean> getBlocked() {
        return blocked;
    }

    /**
     * Getter for the blocked value.
     * 
     * @return the current blocked value.
     */
    public boolean isBlocked() {
        return blocked.get();
    }

    /**
     * Getter for the {@link Property} of the current consumption.
     * @return the property
     */
    public Property<Double> getCurrentConsumptionProperty() {
        return this.currentConsumption;
    }

    /**
     * Getter for the {@link Property} of the overall consumption.
     * @return the property
     */
    public Property<Double> getOverallConsumptionProperty() {
        return this.overallConsumption;
    }
    
    /**
     * Getter for the {@link Property} value of the current consumption.
     * @return the value
     */
    public Double getCurrentConsumption() {
        return this.currentConsumption.get();
    }

    /**
     * Getter for the {@link Property} of the overall consumption.
     * @return the property
     */
    public Double getOverallConsumption() {
        return this.overallConsumption.get();
    }
}
