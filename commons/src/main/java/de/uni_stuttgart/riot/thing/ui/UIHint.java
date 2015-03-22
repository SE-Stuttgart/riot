package de.uni_stuttgart.riot.thing.ui;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
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
public abstract class UIHint {

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
    public static UIHint fromAnnotation(Parameter annotation, java.lang.reflect.Type fieldType, java.lang.reflect.Type valueType) { // NOCS
        if (annotation.ui() == Parameter.NoHint.class) {
            return null;

        } else if (annotation.ui() == IntegralSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            return integralSlider((long) annotation.min(), (long) annotation.max(), (int) annotation.group());

        } else if (annotation.ui() == FractionalSlider.class) {
            if (annotation.min() == Double.MIN_VALUE || annotation.max() == Double.MAX_VALUE) {
                throw new IllegalArgumentException("You must specify min and max for UIHint.IntegralSlider!");
            }
            return fractionalSlider(annotation.min(), annotation.max(), (int) annotation.group());

        } else if (annotation.ui() == PercentageSlider.class) {
            return percentageSlider((int) annotation.group());

        } else if (annotation.ui() == ToggleButton.class) {
            return toggleButton((int) annotation.group());

        } else if (annotation.ui() == EditText.class) {
            return editText((int) annotation.group());

        } else if (annotation.ui() == EditNumber.class) {
            return editNumber((int) annotation.group());

        } else if (annotation.ui() == EnumDropDown.class) {
            if (fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                EnumDropDown result = dropDown((Class<Enum>) fieldType, (int) annotation.group());
                return result;
            } else {
                throw new IllegalArgumentException("UIHint.EnumDropDown can only be used with Enum fields!");
            }

        } else if (annotation.ui() == ReferenceDropDown.class || annotation.ui() == ThingDropDown.class) {

            @SuppressWarnings("unchecked")
            Class<? extends Referenceable<?>> targetType = (Class<? extends Referenceable<?>>) valueType;
            if (annotation.ui() == ReferenceDropDown.class) {
                if (Thing.class.isAssignableFrom(targetType)) {
                    throw new IllegalArgumentException("UIHint.ReferenceDropDown cannot be used for things, use UIHint.ThingReferenceDropDown instead!");
                }
                return referenceDropDown(targetType, (int) annotation.group());
            } else {
                if (!Thing.class.isAssignableFrom(targetType)) {
                    throw new IllegalArgumentException("UIHint.ThingReferenceDropDown can only be used with Reference fields that reference a Thing!");
                }
                @SuppressWarnings("unchecked")
                Class<? extends Thing> thingType = (Class<? extends Thing>) targetType;
                return thingDropDown(thingType, (int) annotation.group(),annotation.requires());
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
     * Returns a IntegralSlider for the given values. See {@link IntegralSlider}.
     * 
     * @param min
     *            The minimum value.
     * @param max
     *            The maximum value.
     * @param groupAndOrderID
     *            The group of this element
     * @return The IntegralSlider.
     */
    public static IntegralSlider integralSlider(long min, long max, int groupAndOrderID) {
        IntegralSlider result = UIHint.integralSlider(min, max);
        result.setgroupAndOrderID(groupAndOrderID);
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
     * @param groupAndOrderID
     *            The group of this element
     * @return The FractionalSlider.
     */
    public static FractionalSlider fractionalSlider(double min, double max, int groupAndOrderID) {
        FractionalSlider result = UIHint.fractionalSlider(min, max);
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
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
     * Returns a PercentageSlider.
     * 
     * @param groupAndOrderID
     *            The group of this element
     * @return The PercentageSlider.
     */
    public static PercentageSlider percentageSlider(int groupAndOrderID) {
        PercentageSlider result = new PercentageSlider();
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
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
     * Returns a ToggleButton.
     * 
     * @return The ToggleButton.
     */
    public static ToggleButton toggleButton(int groupAndOrderID) {
        ToggleButton result = new ToggleButton();
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
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
     * Returns a EditText.
     * 
     * @param groupAndOrderID
     *            The group of this element
     * @return The EditText.
     */
    public static EditText editText(int groupAndOrderID) {
        EditText result = new EditText();
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
    }

    /**
     * The value is a String. The UI could use a simple text field to allow the user to enter arbitrary values for the string. <tt>null</tt>
     * should be avoided, use the empty string instead. In readonly mode, this could be displayed either as a disabled text field or as a
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
     * Returns a EditNumber.
     * 
     * @param groupAndOrderID
     *            The group of this element
     * @return The EditNumber.
     */
    public static EditNumber editNumber(int groupAndOrderID) {
        EditNumber result = new EditNumber();
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
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
     * Returns an EnumDropDown.
     * 
     * @param <E>
     *            The type of the enum.
     * @param theEnum
     *            The type of the enum.
     * @param groupAndOrderID
     *            The group of this element
     * @return The EnumDropDown.
     */
    public static <E extends Enum<E>> EnumDropDown dropDown(Class<E> theEnum, int groupAndOrderID) {
        EnumDropDown result = UIHint.dropDown(theEnum);
        result.setgroupAndOrderID(groupAndOrderID);
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
     * Returns a ReferenceDropDown.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param targetType
     *            The type of the referenced entities.
     * @param groupAndOrderID
     *            The group of this element
     * @return The ReferenceDropDown.
     */
    public static <R extends Referenceable<?>> ReferenceDropDown referenceDropDown(Class<R> targetType, int groupAndOrderID) {
        ReferenceDropDown result = UIHint.referenceDropDown(targetType);
        result.setgroupAndOrderID(groupAndOrderID);
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
     * Returns a ThingReferenceDropDown.
     * 
     * @param <T>
     *            The type of the referenced things.
     * @param targetType
     *            The type of the referenced things.
     * @param requiredPermissions
     *            The permissions that the user needs on the things that can be chosen.
     * @param groupAndOrderID
     *            The group of this element
     * @return The ThingReferenceDropDown.
     */
    public static <T extends Thing> ThingDropDown thingDropDown(Class<T> targetType, int groupAndOrderID, ThingPermission... requiredPermissions) {
        ThingDropDown result = UIHint.thingDropDown(targetType, requiredPermissions);
        result.setgroupAndOrderID(groupAndOrderID);
        return result;
    }

    /**
     * Getter for the groupAndOrderID, could be used by the UI to group elements.
     * 
     * @return the groupAndOrderID of this element.
     */
    public int getgroupAndOrderID() {
        return groupAndOrderID;
    }

    protected void setgroupAndOrderID(int groupAndOrderID) {
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
    }
   
}
