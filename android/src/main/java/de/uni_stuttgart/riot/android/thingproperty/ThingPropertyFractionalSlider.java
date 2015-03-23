package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.text.InputType;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as fractional slider.
 */
public class ThingPropertyFractionalSlider extends ThingPropertySlider<Double> {

    private int factor;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertyFractionalSlider(Property<Double> property, Context context, double min, double max) {
        super(property, context, min, max);
    }

    /**
     * Constructor.
     *
     * @param value   is used for testing
     * @param context is the application context
     * @param min     is the minimum possible value
     * @param max     is the maximum possible value
     */
    public ThingPropertyFractionalSlider(Double value, Context context, double min, double max) {
        super(value, context, min, max);
    }

    @Override
    protected void setValue(Double value) {
        // Round the value with the explicit number of decimal places
        super.editText.setText(String.valueOf(((Long) Math.round(value * this.factor)).doubleValue() / this.factor));
        super.seekBar.setProgress((int) ((value - super.min) * this.factor));
    }

    @Override
    protected void thingsBeforeBuildElement() {
        // Calculate the maximum number of decimal places and the factor to handle floating numbers with the slider
        final int ten = 10;
        int minSplitLen = 0;
        int maxSplitLen = 0;

        String decimalSeparator = "[.,]";

        String[] minSplit = Double.toString(Math.abs(this.min)).split(decimalSeparator);
        if (minSplit.length > 1) {
            minSplitLen = minSplit[1].length();
        }

        String[] maxSplit = Double.toString(Math.abs(this.max)).split(decimalSeparator);
        if (maxSplit.length > 1) {
            maxSplitLen = minSplit[1].length();
        }

        // Save number of decimal places (it should be 1 place in minimum)
        int numberOfDecimalPlaces = Math.max(minSplitLen, maxSplitLen);
        numberOfDecimalPlaces = Math.max(numberOfDecimalPlaces, 1);

        // Save factor to calculate
        this.factor = (int) Math.pow(ten, numberOfDecimalPlaces);
    }

    @Override
    protected Double handleSeekBarValue(Integer value) {
        return (value.doubleValue() / this.factor + super.min);
    }

    @Override
    protected int getRealMax() {
        return (int) ((super.max - super.min) * this.factor);
    }

    @Override
    protected void setEditTextAttributes() {
        if (this.min < 0) {
            super.editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        } else {
            super.editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    protected Double getValueOfEditText() {
        return Double.valueOf(super.editText.getText().toString());
    }

    @Override
    protected boolean isValueValid(Double value) {
        return (value.compareTo(super.max) > 0 || value.compareTo(super.min) < 0);
    }
}
