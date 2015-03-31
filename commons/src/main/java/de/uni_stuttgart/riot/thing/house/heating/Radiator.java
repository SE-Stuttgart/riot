package de.uni_stuttgart.riot.thing.house.heating;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a radiator, which is a part of a heating system.
 */
public class Radiator extends Thing {

    private static final Double DEFAULT_CONFIGURED_TEMP = 22.0;

    /**
     * The configured temperature for this radiator.
     */
    private final WritableProperty<Double> configuredTemp = newWritableProperty("configuredTemp", Double.class, DEFAULT_CONFIGURED_TEMP, UIHint.editNumber());

    /**
     * The currently measured temperature by the radiator.
     */
    private final Property<Double> measuredTemp = newProperty("measuredTemp", Double.class, DEFAULT_CONFIGURED_TEMP, UIHint.editNumber());

    /**
     * Constructor.
     * 
     * @param behavior .
     */
    public Radiator(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the configured temperature property.
     * @return the property
     */
    public WritableProperty<Double> getConfiguredTemp() {
        return configuredTemp;
    }

    /**
     * Getter for the measured temperature property.
     * @return the measuredTemp
     */
    public Property<Double> getMeasuredTemp() {
        return measuredTemp;
    }

}
