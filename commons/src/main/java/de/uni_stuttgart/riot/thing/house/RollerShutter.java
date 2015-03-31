package de.uni_stuttgart.riot.thing.house;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a roller shutter.
 *
 */
public class RollerShutter extends Thing {

    /**
     * A percentage value that indicates the shutter position. (0 -> opened, 100 -> closed)
     */
    private final Property<Double> level = newProperty("level", Double.class, 0.0, UIHint.percentageSlider());

    /**
     * Constructor.
     * 
     * @param behavior
     *            .
     */
    public RollerShutter(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the level property.
     * 
     * @return the level
     */
    public Property<Double> getLevelProperty() {
        return level;
    }

    /**
     * Getter for the level value.
     * @return (0 -> opened, 100 -> closed)
     */
    public double getLevel() {
        return level.get();
    }

}
