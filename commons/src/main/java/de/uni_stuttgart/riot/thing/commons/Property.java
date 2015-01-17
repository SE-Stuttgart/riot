package de.uni_stuttgart.riot.thing.commons;

import de.uni_stuttgart.riot.commons.rest.data.Storable;


public class Property<T> {

    private String name;
    private T value;

    public Property(String name, T value) {
        this.setName(name);
        this.setValue(value);
    }
    
    public Property() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
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
