package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This class represents a property belonging to a {@link Thing}. Note that only instances of {@link WritableProperty} allow for setting the
 * value of the property externally.
 *
 * @param <V>
 *            The type of the property's values.
 */
public class Property<V> {

    private final transient Thing thing;
    private final String name;
    private final Class<V> valueType;
    private final transient UIHint uiHint;
    private final transient PropertyChangeEvent<V> changeEvent;
    private V value;

    /**
     * Creates a new property. Note that the constructor is internal. Things are required to instantiate their properties through
     * {@link Thing#newProperty(String, Class, Object)}.
     * 
     * @param thing
     *            The thing that this property belongs to.
     * @param name
     *            The property name.
     * @param valueType
     *            The type of the property's values.
     * @param value
     *            The initial property value.
     * @param uiHint
     *            The UI hint (optional).
     */
    Property(Thing thing, String name, Class<V> valueType, V value, UIHint uiHint) {
        this.thing = thing;
        this.name = name;
        this.valueType = valueType;
        this.value = value;
        this.changeEvent = new PropertyChangeEvent<V>(this);
        this.uiHint = uiHint;
    }

    /**
     * Gets the containing thing.
     * 
     * @return The thing that this property belongs to.
     */
    public Thing getThing() {
        return thing;
    }

    /**
     * Gets the property name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value type.
     * 
     * @return The type of the property's values.
     */
    public Class<V> getValueType() {
        return valueType;
    }

    /**
     * Gets the property value.
     *
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * Silently sets the new value. This is only for internal use by the framework.
     * 
     * @param newValue
     *            The new value.
     */
    void setValueSilently(V newValue) {
        this.value = newValue;
    }

    /**
     * Gets the PropertyChangeEvent of this property.
     * 
     * @return The PropertyChangeEvent of this property.
     */
    public PropertyChangeEvent<V> getChangeEvent() {
        return changeEvent;
    }

    /**
     * Gets the UIHint.
     * 
     * @return The UI hint for this property.
     */
    public UIHint getUiHint() {
        return uiHint;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(getClass().getSimpleName());
        output.append("[name=");
        output.append(getName());
        output.append(", type=");
        output.append(valueType.getSimpleName());
        output.append(", value=");
        output.append(value);
        output.append("]");
        return output.toString();
    }

    // CHECKSTYLE:OFF (Generated Code)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Property<?> other = (Property<?>) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        if (valueType == null) {
            if (other.valueType != null) {
                return false;
            }
        } else if (!valueType.equals(other.valueType)) {
            return false;
        }
        return true;
    }

}
