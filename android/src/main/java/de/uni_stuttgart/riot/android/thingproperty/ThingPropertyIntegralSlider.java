package de.uni_stuttgart.riot.android.thingproperty;

import android.app.Activity;
import android.text.InputType;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as integral slider.
 */
public class ThingPropertyIntegralSlider extends ThingPropertySlider<Integer> {

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertyIntegralSlider(Property<Integer> property, Activity activity, int min, int max) {
        super(property, activity, min, max);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertyIntegralSlider(Integer value, Activity activity, int min, int max) {
        super(value, activity, min, max);
    }

    @Override
    protected void setValue(Integer value) {
        super.editText.setText(String.valueOf(value));
        super.seekBar.setProgress(value - super.min);
    }

    @Override
    protected void thingsBeforeBuildElement() {
    }

    @Override
    protected Integer handleSeekBarValue(Integer value) {
        return (value + super.min);
    }

    @Override
    protected int getRealMax() {
        return (super.max - super.min);
    }

    @Override
    protected void setEditTextAttributes() {
        if (super.min < 0) {
            super.editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        } else {
            super.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    protected Integer getValueOfEditText() {
        return Integer.valueOf(editText.getText().toString());
    }

    @Override
    protected boolean isValueValid(Integer value) {
        return (value.compareTo(max) > 0 || value.compareTo(min) < 0);
    }
}
