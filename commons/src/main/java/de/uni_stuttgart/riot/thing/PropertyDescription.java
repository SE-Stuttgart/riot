package de.uni_stuttgart.riot.thing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A class to describe a property.
 * 
 * @author Philipp Keck
 */
public class PropertyDescription {

    private final String name;
    private final Class<?> valueType;
    private final UIHint uiHint;
    private final boolean writable;

    /**
     * Instantiates a new PropertyDescription. Use {@link #create(Property)} to retrieve an instance.
     * 
     * @param name
     *            The name of the property.
     * @param valueType
     *            The type of the property's values.
     * @param writable
     *            Whether the property is writable.
     */
    @JsonCreator
    private PropertyDescription(@JsonProperty("name") String name, @JsonProperty("valueType") Class<?> valueType, @JsonProperty("uiHint") UIHint uiHint, @JsonProperty("writable") boolean writable) {
        this.name = name;
        this.valueType = valueType;
        this.uiHint = uiHint;
        this.writable = writable;
    }

    /**
     * Gets the name of the property.
     * 
     * @return The name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value type of the property.
     * 
     * @return The type of the proprety's values.
     */
    public Class<?> getValueType() {
        return valueType;
    }

    /**
     * Gets the UI hint.
     * 
     * @return The UI hint for the property (may be null).
     */
    public UIHint getUiHint() {
        return uiHint;
    }

    /**
     * Gets the writable value.
     * 
     * @return Whether the property is writable.
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Creates a property description from the given property.
     * 
     * @param property
     *            The property to describe.
     * @return The property description.
     */
    public static PropertyDescription create(Property<?> property) {
        return new PropertyDescription(property.getName(), property.getValueType(), property.getUiHint(), property instanceof WritableProperty);
    }

}
