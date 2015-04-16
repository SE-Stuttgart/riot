package de.uni_stuttgart.riot.thing.ui;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;

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
        @Type(value = UIHint.ThingDropDown.class, name = "ThingReferenceDropDown"), //
})
@SuppressWarnings("serial")
public abstract class UIHint implements Serializable {

    /**
     * Could be used by the UI to group or order the elements.
     */
    private int groupAndOrderID;

    /**
     * Gets a UIHint instance from an {@link Parameter} annotation.
     * 
     * @param annotation
     *            The annotation to read from.
     * @param fieldType
     *            The Java type of the field that the annotation was used on. For references, this is Referenc&lt;X&gt;
     * @param valueType
     *            The value type of the field that the annotation was used on. For references, this is the inner type of the reference (X).
     * @return A corresponding {@link UIHint} or <tt>null</tt> if none was set.
     */
    // CHECKSTYLE: CyclomaticComplexity OFF
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static UIHint fromAnnotation(Parameter annotation, java.lang.reflect.Type fieldType, java.lang.reflect.Type valueType) { // NOCS
        UIHint result = null;
        if (annotation.ui() == Parameter.NoHint.class) {
            return null;

        } else if (annotation.ui() == IntegralSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            result = integralSlider((long) annotation.min(), (long) annotation.max());

        } else if (annotation.ui() == FractionalSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            result = fractionalSlider(annotation.min(), annotation.max());

        } else if (annotation.ui() == PercentageSlider.class) {
            result = percentageSlider();

        } else if (annotation.ui() == ToggleButton.class) {
            result = toggleButton();

        } else if (annotation.ui() == EditText.class) {
            result = editText();

        } else if (annotation.ui() == EditNumber.class) {
            result = editNumber();

        } else if (annotation.ui() == EnumDropDown.class) {
            if (fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
                result = dropDown((Class<Enum>) fieldType);
            } else {
                throw new IllegalArgumentException("UIHint.EnumDropDown can only be used with Enum fields!");
            }

        } else if (annotation.ui() == ReferenceDropDown.class || annotation.ui() == ThingDropDown.class) {

            Class<? extends Referenceable<?>> targetType = (Class<? extends Referenceable<?>>) valueType;
            if (annotation.ui() == ReferenceDropDown.class) {
                if (Thing.class.isAssignableFrom(targetType)) {
                    throw new IllegalArgumentException("UIHint.ReferenceDropDown cannot be used for things, use UIHint.ThingReferenceDropDown instead!");
                }
                result = referenceDropDown(targetType);
            } else {
                if (!Thing.class.isAssignableFrom(targetType)) {
                    throw new IllegalArgumentException("UIHint.ThingReferenceDropDown can only be used with Reference fields that reference a Thing!");
                }
                Class<? extends Thing> thingType = (Class<? extends Thing>) targetType;
                result = thingDropDown(thingType, annotation.requires());
            }

        } else {
            throw new IllegalArgumentException("Unsupported UIHint type " + annotation.ui());
        }
        result.setGroupAndOrderID(annotation.group());
        return result;
    }

    /**
     * Sets the group and order id, this number can be used to order or group UI elements. To order the elements just assign ascending
     * numbers to the {@link UIHint}s. To group elements assign the same number to all group members.
     * 
     * @param groupAndOrder
     *            number that should be used to order or group the elements in the ui.
     * @return the {@link UIHint}
     */
    public UIHint groupAndOrder(int groupAndOrder) {
        this.setGroupAndOrderID(groupAndOrder);
        return this;
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
        return fractionalSlider(min, max, 2);
    }

    /**
     * Returns a FractionalSlider for the given values. See {@link FractionalSlider}.
     * 
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param decimalPlaces
     *            The precision that the field should have. Will be used for decimal places only if the slider is combined with a text
     *            field.
     * @return The FractionalSlider.
     */
    public static FractionalSlider fractionalSlider(double min, double max, int decimalPlaces) {
        FractionalSlider result = new FractionalSlider();
        result.min = min;
        result.max = max;
        result.decimalPlaces = decimalPlaces;
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

        /**
         * The precision that the field should have. Will be used for decimal places only if the slider is combined with a text field.
         */
        public int decimalPlaces;
    }

    /**
     * Returns a PercentageSlider with no fractional percentages allowed.
     * 
     * @return The PercentageSlider.
     */
    public static PercentageSlider percentageSlider() {
        return percentageSlider(2);
    }

    /**
     * Returns a PercentageSlider.
     * 
     * @param decimalPlaces
     *            The precision that the field should have. Will be used for decimal places only if the slider is combined with a text
     *            field. Note that the number of decimal places refers to the <tt>1.0</tt> representation and not the <tt>100.0%</tt>.
     * @return The PercentageSlider.
     */
    public static PercentageSlider percentageSlider(int decimalPlaces) {
        PercentageSlider result = new PercentageSlider();
        result.decimalPlaces = decimalPlaces;
        return result;
    }

    /**
     * The acceptable values are between <tt>0.0</tt> and <tt>1.0</tt> and are to be interpreted as percentages (<tt>0%</tt> through
     * <tt>100%</tt>). A draggable slider could be used to display/change the value. In readonly mode, this could be displayed as a disabled
     * slider or as a progress bar.
     */
    public static class PercentageSlider extends UIHint {
        /**
         * The precision that the field should have. Will be used for decimal places only if the slider is combined with a text field. Note
         * that the number of decimal places refers to the <tt>1.0</tt> representation and not the <tt>100.0%</tt>.
         */
        public int decimalPlaces;
    }

    /**
     * Returns a ToggleButton.
     * 
     * @return The ToggleButton.
     */
    public static ToggleButton toggleButton() {
        return new ToggleButton();
    }

    /**
     * The value is a boolean. The UI could use a toggle switch, preferably without labels, or a checkbox. In readonly mode, this could be
     * displayed as a disabled toggle or checkbox.
     */
    public static class ToggleButton extends UIHint {
    }

    /**
     * Returns a EditText.
     * 
     * @return The EditText.
     */
    public static EditText editText() {
        return new EditText();
    }

    /**
     * The value is a String. The UI could use a simple text field to allow the user to enter arbitrary values for the string. <tt>null</tt>
     * should be avoided, use the empty string instead. In readonly mode, this could be displayed either as a disabled text field or as a
     * simple label.
     */
    public static class EditText extends UIHint {
    }

    /**
     * Returns a EditNumber.
     * 
     * @return The EditNumber.
     */
    public static EditNumber editNumber() {
        return editNumber(2);
    }

    /**
     * Returns a EditNumber.
     * 
     * @param decimalPlaces
     *            The number of decimal places to be displayed. Will be ignored for integral fields.
     * @return The EditNumber.
     */
    public static EditNumber editNumber(int decimalPlaces) {
        EditNumber result = new EditNumber();
        result.decimalPlaces = decimalPlaces;
        return result;
    }

    /**
     * The value is numeric, but there is not necessarily a range of possible values. The UI could use a simple text field (possibly with
     * up/down buttons) to allow the user to enter numbers. In readonly mode, this could be displayed either as a disabled text field or as
     * a simple label.
     */
    public static class EditNumber extends UIHint {

        /**
         * The number of decimal places to be displayed. Will be ignored for integral fields.
         */
        public int decimalPlaces;
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

    /**
     * Returns a ThingReferenceDropDown.
     * 
     * @param <T>
     *            The type of the referenced things.
     * @param targetType
     *            The type of the referenced things.
     * @param requiredPermissions
     *            The permissions that the user needs on the things that can be chosen.
     * @return The ThingReferenceDropDown.
     */
    public static <T extends Thing> ThingDropDown thingDropDown(Class<T> targetType, ThingPermission... requiredPermissions) {
        ThingDropDown result = new ThingDropDown();
        result.targetType = targetType;
        result.requiredPermissions = requiredPermissions;
        return result;
    }

    /**
     * Getter for the groupAndOrderID, could be used by the UI to group elements.
     * 
     * @return the groupAndOrderID of this element.
     */
    public int getGroupAndOrderID() {
        return groupAndOrderID;
    }

    protected void setGroupAndOrderID(int groupAndOrderID) {
        this.groupAndOrderID = groupAndOrderID;
    }

    /**
     * The value is a reference to a {@link Thing} (or a special subtype of {@link Thing}). The UI needs to retrieve all possible values and
     * could display them in a dropdown list. In readonly mode, this could be displayed either as a disabled dropdown or as a simple label.
     * The possible values can be limited by the given {@link ThingDropDown#requiredPermissions}. A thing may only be displayed as a
     * possible value if the user has all of the given privileges on the thing.
     */
    public static class ThingDropDown extends ReferenceDropDown {

        /**
         * The permissions that the user needs on the things that can be chosen.
         */
        public ThingPermission[] requiredPermissions;

        /**
         * Gets the permissions as a set.
         * 
         * @return The permissions that the user needs on the things that can be chosen.
         */
        public Set<ThingPermission> getThingPermissions() {
            if (requiredPermissions == null || requiredPermissions.length == 0) {
                return EnumSet.noneOf(ThingPermission.class);
            } else {
                return EnumSet.of(requiredPermissions[0], requiredPermissions);
            }
        }
    }

}
