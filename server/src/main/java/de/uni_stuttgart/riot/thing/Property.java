package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.thing.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.event.PropertyChangeEvent;

public class Property<T> {

    private String name;
    private T value;
    
    private PropertySetAction<T> propertySetAction;
    private PropertyChangeEvent propertyChangeEvent;
    
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

    public PropertySetAction<T> getPropertySetAction() {
        return propertySetAction;
    }

    public void setPropertySetAction(PropertySetAction<T> propertySetAction) {
        this.propertySetAction = propertySetAction;
    }

    public PropertyChangeEvent getPropertyChangeEvent() {
        return propertyChangeEvent;
    }

    public void setPropertyChangeEvent(PropertyChangeEvent propertyChangeEvent) {
        this.propertyChangeEvent = propertyChangeEvent;
    }
    
}
