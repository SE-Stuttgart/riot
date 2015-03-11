package de.uni_stuttgart.riot.thing.ui;

import java.lang.reflect.ParameterizedType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.thing.Parameter;

/**
 * A UIHint is assigned to a property or a field of an action/event instance and provides information to UIs about how to display the value
 * or which controls to use in order to allow the user to change the value.<br>
 * Please note that even though a UI hint may indicate that the field is editable, the property may still not be writable. The UI must
 * respect this and either use a similar representation or at least disable the respective UI control.
 * 
 * @author Philipp Keck
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = UIHint.IntegralSlider.class, name = "IntegralSlider"), //
        @Type(value = UIHint.FractionalSlider.class, name = "FractionalSlider"), //
        @Type(value = UIHint.PercentageSlider.class, name = "PercentageSlider"), //
        @Type(value = UIHint.ToggleButton.class, name = "ToggleButton"), //
        @Type(value = UIHint.EditText.class, name = "EditText"), //
        @Type(value = UIHint.EditNumber.class, name = "EditNumber"), //
        @Type(value = UIHint.EnumDropDown.class, name = "EnumDropDown"), //
        @Type(value = UIHint.ReferenceDropDown.class, name = "ReferenceDropDown"), //
})
public abstract class UIHint {

    /**
     * Gets a UIHint instance from an {@link Parameter} annotation.
     * 
     * @param annotation
     *            The annotation to read from.
     * @param fieldType
     *            The value type of the field that the annotation was used on.
     * @return A corresponding {@link UIHint} or <tt>null</tt> if none was set.
     */
    public static UIHint fromAnnotation(Parameter annotation, java.lang.reflect.Type fieldType) { // NOCS
        if (annotation.ui() == Parameter.NoHint.class) {
            return null;

        } else if (annotation.ui() == IntegralSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            return integralSlider((long) annotation.min(), (long) annotation.max());

        } else if (annotation.ui() == FractionalSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            return fractionalSlider(annotation.min(), annotation.max());

        } else if (annotation.ui() == PercentageSlider.class) {
            return percentageSlider();

        } else if (annotation.ui() == ToggleButton.class) {
            return toggleButton();

        } else if (annotation.ui() == EditText.class) {
            return editText();

        } else if (annotation.ui() == EditNumber.class) {
            return editNumber();

        } else if (annotation.ui() == EnumDropDown.class) {
            if (fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                EnumDropDown result = dropDown((Class<Enum>) fieldType);
                return result;
            } else {
                throw new IllegalArgumentException("UIHint.EnumDropDown can only be used with Enum fields!");
            }

        } else if (annotation.ui() == ReferenceDropDown.class) {
            if (fieldType instanceof ParameterizedType && ((ParameterizedType) fieldType).getRawType() == Reference.class) {
                java.lang.reflect.Type[] typeArguments = ((ParameterizedType) fieldType).getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                    @SuppressWarnings("unchecked")
                    ReferenceDropDown result = referenceDropDown((Class<? extends Referenceable<?>>) typeArguments[0]);
                    return result;
                } else {
                    throw new IllegalArgumentException("The Reference field defines an unexpected type parameter!");
                }
            } else {
                throw new IllegalArgumentException("UIHint.ReferenceDropDown can only be used with Reference fields!");
            }

        } else {
            throw new IllegalArgumentException("Unsupported UIHint type " + annotation.ui());
        }
    }

    /**
     * Returns a IntegralSlider for the given values. See {@link IntegralSlider}.
     * 
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @return The IntegralSlider.
     */
    public static IntegralSlider integralSlider(long min, long max) {
        IntegralSlider result = new IntegralSlider();
        result.min = min;
        result.max = max;
        return result;
    }

    /**
     * The value is numeric and integral (e.g. int or long). The UI could use a draggable slider to display/change the value. Min/Max values
     * are provided to set the range of this slider. In readonly mode, this could be displayed as a disabled slider or as a progress bar.
     */
    public static class IntegralSlider extends UIHint {

        /**
         * The minimum value for the value and thus the lower bound of the slider range.
         */
        public long min;

        /**
         * The maximum value for the value and thus the upper bound of the slider range.
         */
        public long max;
    }

    /**
     * Returns a FractionalSlider for the given values. See {@link FractionalSlider}.
     * 
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @return The FractionalSlider.
     */
    public static FractionalSlider fractionalSlider(double min, double max) {
        FractionalSlider result = new FractionalSlider();
        result.min = min;
        result.max = max;
        return result;
    }

    /**
     * The value is numeric and fractional (e.g. float or double). The UI could use a draggable slider to display/change the value. Min/Max
     * values are provided to set the range of this slider. In readonly mode, this could be displayed as a disabled slider or as a progress
     * bar.
     */
    public static class FractionalSlider extends UIHint {

        /**
         * The minimum value for the value and thus the lower bound of the slider range.
         */
        public double min;

        /**
         * The maximum value for the value and thus the upper bound of the slider range.
         */
        public double max;
    }

    /**
     * Returns a PercentageSlider.
     * 
     * @return The PercentageSlider.
     */
    public static PercentageSlider percentageSlider() {
        return PercentageSlider.INSTANCE;
    }

    /**
     * The acceptable values are between <tt>0.0</tt> and <tt>1.0</tt> and are to be interpreted as percentages (<tt>0%</tt> through
     * <tt>100%</tt>). A draggable slider could be used to display/change the value. In readonly mode, this could be displayed as a disabled
     * slider or as a progress bar.
     */
    public static class PercentageSlider extends UIHint {
        private static final PercentageSlider INSTANCE = new PercentageSlider();
    }

    /**
     * Returns a ToggleButton.
     * 
     * @return The ToggleButton.
     */
    public static ToggleButton toggleButton() {
        return ToggleButton.INSTANCE;
    }

    /**
     * The value is a boolean. The UI could use a toggle switch, preferably without labels, or a checkbox. In readonly mode, this could be
     * displayed as a disabled toggle or checkbox.
     */
    public static class ToggleButton extends UIHint {
        private static final ToggleButton INSTANCE = new ToggleButton();
    }

    /**
     * Returns a EditText.
     * 
     * @return The EditText.
     */
    public static EditText editText() {
        return EditText.INSTANCE;
    }

    /**
     * The value is a String. The UI could use a simple text field to allow the user to enter arbitrary values for the string. <tt>null</tt>
     * should be avoided, use the empty string inst)ead. In readonly mode, this could be displayed either as a disabled text field or as a
     * simple label.
     */
    public static class EditText extends UIHint {
        private static final EditText INSTANCE = new EditText();
    }

    /**
     * Returns a EditNumber.
     * 
     * @return The EditNumber.
     */
    public static EditNumber editNumber() {
        return EditNumber.INSTANCE;
    }

    /**
     * The value is numeric, but there is not necessarily a range of possible values. The UI could use a simple text field (possibly with
     * up/down buttons) to allow the user to enter numbers. In readonly mode, this could be displayed either as a disabled text field or as
     * a simple label.
     */
    public static class EditNumber extends UIHint {
        private static final EditNumber INSTANCE = new EditNumber();
    }

    /**
     * Returns an EnumDropDown.
     * 
     * @param <E>
     *            The type of the enum.
     * @param theEnum
     *            The type of the enum.
     * @return The EnumDropDown.
     */
    public static <E extends Enum<E>> EnumDropDown dropDown(Class<E> theEnum) {
        EnumDropDown result = new EnumDropDown();
        result.possibleValues = theEnum.getEnumConstants();
        return result;
    }

    /**
     * The value is an enum, where the user selects one of multiple items. A list of all possible values is provided. In readonly mode, this
     * could be displayed either as a disabled dropdown or as a simple label.
     */
    public static class EnumDropDown extends UIHint {

        /**
         * Lists all possible values for the enum, i.e. the values to be displayed in the dropdown list.
         */
        public Enum<?>[] possibleValues;
    }

    /**
     * Returns a ReferenceDropDown.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param targetType
     *            The type of the referenced entities.
     * @return The ReferenceDropDown.
     */
    public static <R extends Referenceable<?>> ReferenceDropDown referenceDropDown(Class<R> targetType) {
        ReferenceDropDown result = new ReferenceDropDown();
        result.targetType = targetType;
        return result;
    }

    /**
     * The value is a reference to a certain kind of {@link Referenceable}. The UI needs to retrieve all possible values of this
     * {@link ReferenceDropDown#targetType} and could display them in a dropdown list. In readonly mode, this could be displayed either as a
     * disabled dropdown or as a simple label.
     */
    public static class ReferenceDropDown extends UIHint {

        /**
         * The type of the referenced entity.
         */
        public Class<? extends Referenceable<?>> targetType;
    }

}
