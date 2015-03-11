package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A class to describe a parameter of a {@link BaseInstance}, that is of an event or action instance. A parameter is simply a field declared
 * in subclasses or {@link EventInstance} and {@link ActionInstance}.
 * 
 * @author Philipp Keck
 */
public class ParameterDescription {

    private final String name;
    private final boolean isReference;
    private final Class<?> valueType;
    private final UIHint uiHint;

    /**
     * Instantiates a new {@link ParameterDescription}. Use {@link #create(Field)} to retrieve an instance.
     * 
     * @param name
     *            The name of this parameter.
     * @param isReference
     *            If true, the parameter is actually a {@link Reference}. In this case, the <tt>valueType</tt> will specify the kind of
     *            {@link Referenceable} that is referenced. The actual value transmitted in JSON in an instance is just a number (the ID of
     *            the referenced entity).
     * @param valueType
     *            The value type of this parameter.
     * @param uiHint
     *            The UI hint for this parameter (may be null).
     */
    @JsonCreator
    private ParameterDescription(@JsonProperty("name") String name, @JsonProperty("isReference") boolean isReference, @JsonProperty("valueType") Class<?> valueType, @JsonProperty("uiHint") UIHint uiHint) {
        this.name = name;
        this.isReference = isReference;
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
     * Gets the flag that indicates if the parameter is actually a reference to an entity of type {@link #getValueType()}.
     * 
     * @return True if the parameter is a reference.
     */
    @JsonProperty("isReference")
    public boolean isReference() {
        return isReference;
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
     * Creates a new parameter description from the given parameter field. The type of the field is evaluated to fill the
     * {@link #getValueType()} information. Primitive types like <tt>int</tt> will be converted to <tt>java.lang.Integer</tt>. Reference
     * types like <tt>{@link Reference}&lt;Something&gt;</tt> will be converted to <tt>Something</tt> with {@link #isReference()} set to
     * true.
     * 
     * @param field
     *            The field (should be in a subclass of {@link BaseInstance}, this is not checked, however).
     * @return The parameter description.
     */
    public static ParameterDescription create(Field field) {
        UIHint uiHint = null;
        Type fieldType = field.getGenericType();
        if (field.isAnnotationPresent(Parameter.class)) {
            try {
                uiHint = UIHint.fromAnnotation(field.getAnnotation(Parameter.class), fieldType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error reading the UI hint for field " + field, e);
            }
        }

        Class<?> valueType;
        boolean isReference = false;
        if (fieldType instanceof Class) {
            valueType = getBoxedType((Class<?>) fieldType);
        } else if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            if (!(parameterizedType.getRawType() instanceof Class)) {
                throw new IllegalArgumentException("Unsupported field type " + fieldType + " of field " + field);
            }

            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (rawType == Reference.class) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length != 1 || !(typeArguments[0] instanceof Class)) {
                    throw new IllegalArgumentException("Illegal type argument(s) for reference field " + field);
                }
                valueType = (Class<?>) typeArguments[0];
                isReference = true;
            } else if (Reference.class.isAssignableFrom(rawType)) {
                throw new IllegalArgumentException("Reference parameters need to be defined with 'Reference' and no sub-class or sub-interface of it. See " + field);
            } else if (Collection.class.isAssignableFrom(rawType)) {
                throw new IllegalArgumentException("Collections as parameters are not (yet) supported. See " + field);
            } else {
                throw new IllegalArgumentException("Unsupported parameter type " + rawType + " of field " + field);
            }
        } else {
            throw new IllegalArgumentException("Unsupported field type " + fieldType + " of field " + field);
        }
        return new ParameterDescription(field.getName(), isReference, valueType, uiHint);
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
