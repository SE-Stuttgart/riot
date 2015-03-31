package de.uni_stuttgart.riot.android.managementproperty;

import android.app.Activity;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as percentage slider.
 */
public class ManagementPropertyPercentageSlider extends ManagementPropertyFractionalSlider {

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     */
    public ManagementPropertyPercentageSlider(Property<Double> property, Activity activity) {
        super(property, activity, 0.0, 1.0);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     */
    public ManagementPropertyPercentageSlider(Double value, Activity activity) {
        super(value, activity, 0.0, 1.0);
    }
}
