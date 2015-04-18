package de.uni_stuttgart.riot.android.ui;

import java.math.BigDecimal;
import java.text.NumberFormat;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import de.uni_stuttgart.riot.android.Callback;

/**
 * An {@link EditText} field that converts its value to a number and makes it accessible as a property.
 * 
 * @param <V>
 *            The value type of the field (must be a number, preferably from <tt>java.lang</tt>, should specify a static <tt>valueOf</tt>
 *            method).
 */
public class EditNumberView<V extends Number> extends EditText implements PropertyView<V> {

    private final Class<V> valueType;
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
     *            The number of decimal places to be displayed.
     */
    public EditNumberView(Context context, Class<V> valueType, boolean allowNegative, int decimalPlaces) {
        super(context);
        this.valueType = valueType;

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
        super.setInputType(inputType);

        super.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                Callback<V> l = listener;
                if (l != null) {
                    l.callback(getValue());
                }
            }
        });
    }

    @Override
    public void setValue(V value) {
        super.setText(value == null ? "" : formatter.format(value));
    }

    @Override
    public V getValue() {
        String strValue = super.getText().toString();
        if (strValue == null || strValue.isEmpty()) {
            return null;
        }
        try {
            return valueType.cast(valueType.getMethod("valueOf", String.class).invoke(null, strValue));
        } catch (Exception e) {
            throw new RuntimeException("Cannot call " + valueType.getName() + "#valueOf(" + strValue + ")", e);
        }
    }

    @Override
    public void setListener(Callback<V> listener) {
        this.listener = listener;
    }

    @Override
    public View toView() {
        return this;
    }

    /**
     * Creates a new EditNumberView. This is a workaround method to allow Java 6 to bind the type parameter throug the <tt>valueType</tt>
     * paramter without using the diamond operator.
     * 
     * @param <V>
     *            The concrete number type.
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete number type (must be one of the builtin Java types).
     * @param allowNegative
     *            Whether negative values are permitted.
     * @param decimalPlaces
     *            The number of decimal places to be displayed.
     * @return The view.
     */
    public static <V extends Number> EditNumberView<V> create(Context context, Class<V> valueType, boolean allowNegative, int decimalPlaces) {
        return new EditNumberView<V>(context, valueType, allowNegative, decimalPlaces);
    }

}
