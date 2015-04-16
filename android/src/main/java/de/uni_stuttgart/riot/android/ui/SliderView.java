package de.uni_stuttgart.riot.android.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import de.uni_stuttgart.riot.android.Callback;

/**
 * A {@link SeekBar} combined with an {@link EditText} field that are synced and convert their value to a number and make it accessible as a
 * property. Note that the Android-slider will only have half the precision of the integer range.
 * 
 * @param <V>
 *            The value type of the field (must be a number, preferably from <tt>java.lang</tt>, should specify a static <tt>valueOf</tt>
 *            method).
 */
public abstract class SliderView<V extends Number> extends LinearLayout implements PropertyView<V> {

    private static final float SLIDER_LAYOUT_WEIGHT = 0.2f;

    protected final Class<V> valueType;
    protected final SeekBar seekBar;
    protected final EditText editText;
    private final NumberFormat formatter;
    private Callback<V> listener;

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete number type (must be one of the builtin Java types).
     * @param allowNegative
     *            Whether negative values are permitted.
     * @param decimalPlaces
     *            The number of decimal places to be displayed in the text view.
     */
    public SliderView(Context context, Class<V> valueType, boolean allowNegative, int decimalPlaces) {
        super(context);
        this.valueType = valueType;
        super.setOrientation(HORIZONTAL);

        this.seekBar = new SeekBar(context);
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                if (fromUser) {
                    editText.setText(formatter.format(seekBarToValue(progress)));
                }
            }

            public void onStartTrackingTouch(SeekBar s) {
            }

            public void onStopTrackingTouch(SeekBar s) {
                V value = seekBarToValue(s.getProgress());
                editText.setText(formatter.format(value));
                notifyListener(value);
            }
        });

        this.editText = new EditText(context);
        int inputType = InputType.TYPE_CLASS_NUMBER;
        if (allowNegative) {
            inputType |= InputType.TYPE_NUMBER_FLAG_SIGNED;
        }
        if (valueType == Float.class || valueType == Double.class || valueType == BigDecimal.class) {
            inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
            this.formatter = NumberFormat.getNumberInstance();
            this.formatter.setMaximumFractionDigits(decimalPlaces);
        } else {
            this.formatter = NumberFormat.getIntegerInstance();
        }
        this.editText.setInputType(inputType);
        this.editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                V value = getValue();
                if (value == null) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress(valueToSeekBar(value));
                }
                notifyListener(value);
            }
        });

        LayoutParams seekBarLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        seekBarLayout.gravity = Gravity.CENTER;
        seekBarLayout.weight = SLIDER_LAYOUT_WEIGHT;
        this.seekBar.setLayoutParams(seekBarLayout);
        addView(seekBar);

        LayoutParams editTextLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        editTextLayout.gravity = Gravity.CENTER;
        editTextLayout.weight = 1f - SLIDER_LAYOUT_WEIGHT;
        this.editText.setLayoutParams(editTextLayout);
        addView(editText);

    }

    @Override
    public void setValue(V value) {
        editText.setText(formatter.format(value));
        seekBar.setProgress(valueToSeekBar(value));
    }

    @Override
    public V getValue() {
        String strValue = editText.getText().toString();
        if (strValue == null || strValue.isEmpty()) {
            return null;
        }
        try {
            return numberToValue(formatter.parse(strValue));
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void setListener(Callback<V> listener) {
        this.listener = listener;
    }

    /**
     * Calls the listener, thread-safe.
     * 
     * @param value
     *            The value to send.
     */
    protected void notifyListener(V value) {
        Callback<V> l = listener;
        if (l != null) {
            l.callback(value);
        }
    }

    /**
     * Converts a seek bar progress value to the value type of the underlying property.
     * 
     * @param progress
     *            The seek bar progress (0 through max).
     * @return The converted value.
     */
    protected abstract V seekBarToValue(int progress);

    /**
     * Converts a property value to the respective seek bar progress.
     * 
     * @param value
     *            The property value.
     * @return The seek bar progress.
     */
    protected abstract int valueToSeekBar(V value);

    /**
     * Converts the given number to the correct value type.
     * 
     * @param number
     *            The number.
     * @return The converted value.
     */
    protected abstract V numberToValue(Number number);

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editText.setEnabled(enabled);
        seekBar.setEnabled(enabled);
    }

    @Override
    public View toView() {
        return this;
    }

}
