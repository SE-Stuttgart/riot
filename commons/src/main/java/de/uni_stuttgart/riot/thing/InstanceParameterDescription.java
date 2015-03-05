package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A class to describe a parameter of a {@link BaseInstance}, that is of an event or action instance. A parameter is simply a field declared
 * in subclasses or {@link EventInstance} and {@link ActionInstance}.
 * 
 * @author Philipp Keck
 */
public class InstanceParameterDescription {

    private final String name;
    private final Class<?> valueType;
    private final UIHint uiHint;

    /**
     * Instantiates a new {@link InstanceParameterDescription}. Use {@link #create(Field)} to retrieve an instance.
     * 
     * @param name
     *            The name of this parameter.
     * @param valueType
     *            The value type of this parameter.
     * @param uiHint
     *            The UI hint for this parameter (may be null).
     */
    @JsonCreator
    private InstanceParameterDescription(@JsonProperty("name") String name, @JsonProperty("valueType") Class<?> valueType, @JsonProperty("uiHint") UIHint uiHint) {
        this.name = name;
        this.valueType = valueType;
        this.uiHint = uiHint;
    }

    /**
     * Gets the name.
     * 
     * @return The name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value type.
     * 
     * @return The value type of this parameter.
     */
    public Class<?> getValueType() {
        return valueType;
    }

    /**
     * Gets the UI hint.
     * 
     * @return The UI hint for this parameter (may be null).
     */
    public UIHint getUiHint() {
        return uiHint;
    }

    /**
     * Creates a new parameter description from the given parameter field.
     * 
     * @param field
     *            The field (should be in a subclass of {@link BaseInstance}, this is not checked, however).
     * @return The parameter description.
     */
    public static InstanceParameterDescription create(Field field) {
        UIHint uiHint = null;
        Class<?> fieldType = getBoxedType(field.getType());
        if (field.isAnnotationPresent(InstanceParameter.class)) {
            uiHint = UIHint.fromAnnotation(field.getAnnotation(InstanceParameter.class), fieldType);
        }
        return new InstanceParameterDescription(field.getName(), fieldType, uiHint);
    }

    /**
     * For primitive types (like <tt>int</tt>), this method returns the corresponding boxing type (like <tt>java.lang.Integer</tt>), all
     * other types remain unchanged.
     * 
     * @param type
     *            The (possibly primitive) type.
     * @return The certainly non-primitive type.
     */
    public static Class<?> getBoxedType(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        } else if (type == Integer.TYPE) {
            return Integer.class;
        } else if (type == Long.TYPE) {
            return Long.class;
        } else if (type == Float.TYPE) {
            return Float.class;
        } else if (type == Double.TYPE) {
            return Double.class;
        } else if (type == Boolean.TYPE) {
            return Boolean.class;
        } else if (type == Byte.TYPE) {
            return Byte.class;
        } else if (type == Character.TYPE) {
            return Character.class;
        } else if (type == Short.TYPE) {
            return Short.class;
        } else {
            return type;
        }
    }

}
