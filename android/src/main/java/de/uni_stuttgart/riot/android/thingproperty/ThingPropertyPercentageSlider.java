package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as percentage slider.
 */
public class ThingPropertyPercentageSlider extends ThingPropertyFractionalSlider {

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     */
    public ThingPropertyPercentageSlider(Property<Double> property, Context context) {
        super(property, context, 0.0, 1.0);
    }

    /**
     * Constructor.
     *
     * @param value   is used for testing
     * @param context is the application context
     */
    public ThingPropertyPercentageSlider(Double value, Context context) {
        super(value, context, 0.0, 1.0);
    }
}
