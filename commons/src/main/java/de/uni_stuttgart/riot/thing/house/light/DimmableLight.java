package de.uni_stuttgart.riot.thing.house.light;

import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a dimmable light.
 *
 */
public class DimmableLight extends Light {

    /**
     * A percentage value indicating dimm state of the light, where 0% means no light emission and 100% full light emission
     */
    private final WritableProperty<Double> dimmLevel = newWritableProperty("dimmLevel", Double.class, 0.5, UIHint.percentageSlider());

    /**
     * Constructor.
     * @param behavior .
     */
    public DimmableLight(ThingBehavior behavior) {
        super(behavior);
    }

}
