package de.uni_stuttgart.riot.thing.house.light;

import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a simple light that could be switched on and off.
 *
 */
public class Light extends Thing {

    /**
     * The on/off switch, where false is off.
     */
    private final WritableProperty<Boolean> lightSwitch = newWritableProperty("lightSwitch", Boolean.class, false, UIHint.toggleButton());

    /**
     * Constructor for the {@link Light}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public Light(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the switch value.
     * 
     * @return true if on false otherwise.
     */
    public boolean isOn() {
        return lightSwitch.get();
    }

    /**
     * Setter for the light switch.
     * 
     * @param on
     *            (true -> on, false -> off)
     */
    public void setOn(boolean on) {
        lightSwitch.set(on);
    }

    /**
     * Getter for the lightSwitch property.
     * 
     * @return the lightSwitch
     */
    public WritableProperty<Boolean> getLightSwitch() {
        return lightSwitch;
    }

}
