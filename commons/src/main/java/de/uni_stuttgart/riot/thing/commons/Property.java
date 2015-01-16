package de.uni_stuttgart.riot.thing.commons;

import de.uni_stuttgart.riot.commons.rest.data.Storable;


public class Property<T> extends Storable{

    private String name;
    private T value;
    private String type;
    private long thingId;
    
    
    public Property(String name, T value) {
        this.setName(name);
        this.setValue(value);
        this.setType(value.getClass().getSimpleName()); //FIXME
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

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
