package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

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
     * @param fieldType
     *            The type of the field (possibly pre-processed).
     * @return The parameter description.
     */
    public static ParameterDescription create(Field field, Type fieldType) {
        UIHint uiHint = null;
        if (field.isAnnotationPresent(Parameter.class)) {
            try {
                uiHint = UIHint.fromAnnotation(field.getAnnotation(Parameter.class), fieldType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error reading the UI hint for field " + field, e);
            }
        }

        // fieldType could be anything. Here we resolve "Reference<X>" to "X".
        Type genericValueType;
        boolean isReference = false;
        if (ClassUtils.isAssignable(field.getType(), Reference.class)) {
            isReference = true;
            genericValueType = TypeUtils.getTypeArguments(fieldType, Reference.class).get(Reference.class.getTypeParameters()[0]);
        } else {
            genericValueType = fieldType;
        }

        // Here we only allow plain classes, i.e. "YClass<Y>" is kicked out.
        Class<?> valueType;
        if (genericValueType instanceof Class) {
            // If it is a plain class, we convert int -> java.lang.Integer and so on, if necessary.
            valueType = ClassUtils.primitiveToWrapper((Class<?>) genericValueType);
        } else if (TypeUtils.isAssignable(genericValueType, Collection.class)) {
            throw new IllegalArgumentException("Collections as parameters are not (yet) supported. See " + field);
        } else {
            throw new IllegalArgumentException("Generic types are not allowed. Please only use Reference<X> where X is a plain class and do not use any other generic types! See " + field);
        }

        return new ParameterDescription(field.getName(), isReference, valueType, uiHint);
    }

}
