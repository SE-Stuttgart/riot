package de.uni_stuttgart.riot.thing.house.light;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a dimmable light.
 *
 */
public class DimmableLight extends Light {

    /**
     * A percentage value indicating dim state of the light, where 0% means no light emission and 100% full light emission
     */
    private final WritableProperty<Double> dimLevel = newWritableProperty("dimLevel", Double.class, 0.5, UIHint.percentageSlider());

    /**
     * Constructor for the {@link DimmableLight}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public DimmableLight(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the dim level property.
     * 
     * @return the dimLevelproperty
     */
    public WritableProperty<Double> getDimmLevelProperty() {
        return dimLevel;
    }

    /**
     * Getter for the dim level value.
     * 
     * @return the dimLevel value
     */
    public Double getDimLevel() {
        return dimLevel.get();
    }

    /**
     * Setter for the dim level.
     * 
     * @param dimLevelVal
     *            0% means no light emission and 100% full light emission
     */
    public void setDimLevel(double dimLevelVal) {
        this.dimLevel.set(dimLevelVal);
    }

}
