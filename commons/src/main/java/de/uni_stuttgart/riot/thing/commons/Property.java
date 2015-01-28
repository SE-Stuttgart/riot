package de.uni_stuttgart.riot.thing.commons;

/**
 * This class represents a property that a {@link Thing} has. It contains the property name and type.
 *
 * @param <T>
 *            property type.
 */
public class Property<T> {

    private String name;
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

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
