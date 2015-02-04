package de.uni_stuttgart.riot.thing.commons;

/**
 * This class represents a property belonging to a {@link Thing}.
 *
 * @param <T>
 *            the property type.
 */
public class Property<T> {

    /** The property name. */
    private String name;

    /** The property value. */
    private T value;

    /**
     * Contructor.
     * 
     * @param name
     *            property name.
     * @param value
     *            property value.
     */
    public Property(String name, T value) {
        this.setName(name);
        this.setValue(value);
    }

    /**
     * Constructor.
     */
    public Property() {
    }

    /**
     * gets the property name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the property name.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the property value.
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the property value.
     *
     * @param value
     *            the new value
     */
    public void setValue(T value) {
        this.value = value;
    }

}
