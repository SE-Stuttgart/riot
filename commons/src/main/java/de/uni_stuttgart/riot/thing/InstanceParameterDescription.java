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
        if (field.isAnnotationPresent(InstanceParameter.class)) {
            uiHint = UIHint.fromAnnotation(field.getAnnotation(InstanceParameter.class), field.getType());
        }
        return new InstanceParameterDescription(field.getName(), field.getType(), uiHint);
    }

}
