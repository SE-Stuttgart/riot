package de.uni_stuttgart.riot.android.ui;

import android.content.Context;

/**
 * A {@link SliderView} for integral values.
 * 
 * @param <V>
 *            The value type of the field (must be {@link Integer} or {@link Long}.
 */
public class IntegralSliderView<V extends Number> extends SliderView<V> {

    private final long min;

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
     */
    public IntegralSliderView(Context context, Class<V> valueType, long min, long max) {
        super(context, valueType, min < 0, 0);
        if (valueType != Integer.class && valueType != Long.class) {
            throw new IllegalArgumentException("Only byte, short, integer and long are supported!");
        }
        this.min = min;
        if (max - min > (long) Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The range is too large for an Android slider!");
        }
        super.seekBar.setMax((int) (max - min));
    }

    @Override
    protected V seekBarToValue(int progress) {
        return numberToValue(progress + min);
    }

    @Override
    protected int valueToSeekBar(V value) {
        return (int) (value.longValue() - min);
    }

    @Override
    protected V numberToValue(Number number) {
        if (valueType == Integer.class) {
            return valueType.cast(number.intValue());
        } else if (valueType == Long.class) {
            return valueType.cast(number.longValue());
        } else {
            throw new IllegalArgumentException("number must be of a supported type!");
        }
    }

    /**
     * Produces an {@link IntegralSliderView}. This is a workaround method to allow Java 6 to bind the type parameter throug the
     * <tt>valueType</tt> paramter without using the diamond operator.
     * 
     * @param <V>
     *            The concrete number type.
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete number type ({@link Integer} or {@link Long}).
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @return The slider.
     */
    public static <V extends Number> IntegralSliderView<V> create(Context context, Class<V> valueType, long min, long max) {
        return new IntegralSliderView<V>(context, valueType, min, max);
    }

}
