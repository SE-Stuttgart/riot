package de.uni_stuttgart.riot.android.ui;

import android.content.Context;

/**
 * A {@link SliderView} for fractional values.
 * 
 * @param <V>
 *            The value type of the field (must be {@link Float} or {@link Double}).
 */
public class FractionalSliderView<V extends Number> extends SliderView<V> {

    private final double min;
    private final double max;
    private final int factor;

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete number type.
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param decimalPlaces
     *            The number of decimal places to be displayed in the text view.
     */
    public FractionalSliderView(Context context, Class<V> valueType, double min, double max, int decimalPlaces) {
        super(context, valueType, min < 0, decimalPlaces);
        if (valueType != Float.class && valueType != Double.class) {
            throw new IllegalArgumentException("Only float and double are supported!");
        }
        this.min = min;
        this.max = max;
        this.factor = (int) Math.pow(10, decimalPlaces); // NOCS
        super.seekBar.setMax((int) ((max - min) * (double) this.factor));
    }

    @Override
    protected V seekBarToValue(int progress) {
        double value = (double) progress / this.factor + min;
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        return numberToValue(value);
    }

    @Override
    protected int valueToSeekBar(V value) {
        return (int) ((value.doubleValue() - min) * this.factor);
    }

    @Override
    protected V numberToValue(Number number) {
        if (valueType == Double.class) {
            return valueType.cast(number.doubleValue());
        } else if (valueType == Float.class) {
            return valueType.cast(number.floatValue());
        } else {
            throw new IllegalArgumentException("number must be of a supported type!");
        }
    }

    /**
     * Produces a {@link FractionalSliderView}. This is a workaround method to allow Java 6 to bind the type parameter throug the
     * <tt>valueType</tt> paramter without using the diamond operator.
     * 
     * @param <V>
     *            The concrete number type.
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete number type ({@link Float} or {@link Double}).
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param decimalPlaces
     *            The number of decimal places to be displayed in the text view.
     * @return The slider.
     */
    public static <V extends Number> FractionalSliderView<V> create(Context context, Class<V> valueType, double min, double max, int decimalPlaces) {
        return new FractionalSliderView<V>(context, valueType, min, max, decimalPlaces);
    }

}
