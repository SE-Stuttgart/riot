package de.uni_stuttgart.riot.android.ui;

import android.content.Context;
import de.uni_stuttgart.riot.thing.ui.UIHint;
import de.uni_stuttgart.riot.thing.ui.UIHint.EnumDropDown;
import de.uni_stuttgart.riot.thing.ui.UIHint.EditNumber;
import de.uni_stuttgart.riot.thing.ui.UIHint.EditText;
import de.uni_stuttgart.riot.thing.ui.UIHint.FractionalSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.IntegralSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.PercentageSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.ToggleButton;

/**
 * Helper class for producing Android UI elements for Thing properties.
 * 
 * @author Philipp Keck
 */
// CHECKSTYLE: CyclomaticComplexity OFF
// CHECKSTYLE: JavaNCSS OFF
public abstract class UIProducer {

    /**
     * Cannot instantiate.
     */
    private UIProducer() {
    }

    /**
     * Creates a {@link PropertyView} for the given UI hint and value type.
     * 
     * @param <T>
     *            The type of the property values.
     * @param context
     *            The Android context.
     * @param hint
     *            The UI hint.
     * @param valueType
     *            The type of the property values.
     * @param label
     *            Some UIs require a label (which is usually the name of the property that the UI is used for).
     * @return The view.
     */
    @SuppressWarnings("unchecked")
    public static <T> PropertyView<T> producePropertyView(Context context, UIHint hint, Class<T> valueType, String label) {
        if (hint instanceof IntegralSlider) {
            IntegralSlider uiHint = (IntegralSlider) hint;
            if (valueType != Integer.class && valueType != Long.class) {
                throw new IllegalArgumentException("IntegralSlider can only be used with Integer or Long, not " + valueType);
            } else {
                return (PropertyView<T>) IntegralSliderView.create(context, valueType.asSubclass(Number.class), uiHint.min, uiHint.max);
            }

        } else if (hint instanceof FractionalSlider) {
            FractionalSlider uiHint = (FractionalSlider) hint;
            if (valueType != Float.class && valueType != Double.class) {
                throw new IllegalArgumentException("FractionalSlider can only be used with Float or Double, not " + valueType);
            } else {
                return (PropertyView<T>) FractionalSliderView.create(context, valueType.asSubclass(Number.class), uiHint.min, uiHint.max, uiHint.decimalPlaces);
            }

        } else if (hint instanceof PercentageSlider) {
            PercentageSlider uiHint = (PercentageSlider) hint;
            if (valueType != Float.class && valueType != Double.class) {
                throw new IllegalArgumentException("PercentageSlider can only be used with Float or Double, not " + valueType);
            } else {
                return (PropertyView<T>) FractionalSliderView.create(context, valueType.asSubclass(Number.class), 0.0, 1.0, uiHint.decimalPlaces);
            }

        } else if (hint instanceof ToggleButton) {
            if (valueType != Boolean.class) {
                throw new IllegalArgumentException("Cannot use ToggleButton for type " + valueType);
            }
            return (PropertyView<T>) new ToggleButtonView(context, label, label);

        } else if (hint instanceof EditText) {
            if (valueType != String.class) {
                throw new IllegalArgumentException("Cannot use EditText for type " + valueType);
            }
            return (PropertyView<T>) new EditTextView(context);

        } else if (hint instanceof EditNumber) {
            if (!Number.class.isAssignableFrom(valueType)) {
                throw new IllegalArgumentException("Cannot use EditNumber for type " + valueType);
            } else {
                return (PropertyView<T>) EditNumberView.create(context, valueType.asSubclass(Number.class), true, ((EditNumber) hint).decimalPlaces);
            }

        } else if (hint instanceof EnumDropDown) {
            if (!Enum.class.isAssignableFrom(valueType)) {
                throw new IllegalArgumentException("DropDown can only be used for enums, not for " + valueType);
            }
            return (PropertyView<T>) EnumDropDownView.create(context, valueType.asSubclass(Enum.class));

        } else {
            return new ToStringView<T>(context);
        }
    }

}
