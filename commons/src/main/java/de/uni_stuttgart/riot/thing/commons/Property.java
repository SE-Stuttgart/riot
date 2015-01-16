package de.uni_stuttgart.riot.thing.commons;


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
    
}
