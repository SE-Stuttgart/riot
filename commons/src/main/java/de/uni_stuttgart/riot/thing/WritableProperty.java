package de.uni_stuttgart.riot.thing;

/**
 * This class represents a writable property belonging to a {@link Thing}. Writable means that there is a {@link PropertySetAction} assigned
 * to this property that allows the user to change the value of the property.
 *
 * @param <V>
 *            The type of the property's values.
 */
public class WritableProperty<V> extends Property<V> {

    private final transient PropertySetAction<V> setAction;

    /**
     * Creates a new writable property. Note that the constructor is internal. Things are required to instantiate their properties through
     * {@link Thing#newWritableProperty(String, Class, Object)}.
     * 
     * @param thing
     *            The thing that this property belongs to.
     * @param name
     *            The property name.
     * @param valueType
     *            The type of the property's values.
     * @param value
     *            The initial property value.
     */
    WritableProperty(Thing thing, String name, Class<V> valueType, V value) {
        super(thing, name, valueType, value);
        this.setAction = new PropertySetAction<V>(this);
    }

    /**
     * Gets the PropertySetAction of this property.
     * 
     * @return The PropertySetAction of this property.
     */
    public PropertySetAction<V> getSetAction() {
        return setAction;
    }

    /**
     * Changes the value of the property. This method is to be called by the user. It is important to note that this method <b>does not
     * actually change</b> the property. Instead, it transports the change to the computer where the thing is actually executed. The change
     * will be performed there and the results and effects will be transported back in the form of events.
     * 
     * @param newValue
     *            The new value for the property.
     */
    public void set(V newValue) {
        getThing().getBehavior().userModifiedProperty(this, newValue);
    }

}
