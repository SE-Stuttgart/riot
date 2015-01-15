package de.uni_stuttgart.riot.thing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import de.uni_stuttgart.riot.thing.action.Action;
import de.uni_stuttgart.riot.thing.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.event.Event;
import de.uni_stuttgart.riot.thing.event.EventListener;
import de.uni_stuttgart.riot.thing.event.PropertyChange;
import de.uni_stuttgart.riot.thing.event.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.house.TestListener;

public abstract class Thing {
    
    private Map<String,Property> properties;
    private Collection<Action> actions;
    private Collection<Event> events;
    private Thing parent;
    
    public Thing(String name, Long id) {
        this.actions = new ArrayList<Action>();
        this.events = new ArrayList<Event>();
        this.properties = new HashMap<String,Property>();
        this.addProperty("name", name);
        this.addProperty("id", id);
        this.initActions();
        this.initEvents();
        this.initProperties();
    }
    
    protected abstract void initProperties();
    protected abstract void initActions();
    protected abstract void initEvents();

    protected <T> EventListener<PropertyChange<T>> addProperty(String name, T defaultValue){
        PropertyChangeEvent<T> changeEvent = new PropertyChangeEvent<T>();
        PropertySetAction<T> changeAction = new PropertySetAction<T>();
        Property<T> property = new Property<T>(name,defaultValue,changeAction, changeEvent);
        changeAction.setProperty(property);
        this.actions.add(changeAction);
        this.events.add(changeEvent);
        this.properties.put(name,property);
        EventListener<PropertyChange<T>> listener = new TestListener<T>();
        changeEvent.register(listener);
        return listener;
    }
    
    protected <T> void changeProperty(String name, T newValue){
        Property<T> property = this.properties.get(name);
        PropertySetAction<T> action = property.getPropertySetAction();
        T oldValue = property.getValue();
        action.execute(action.new PropertySet<T>(newValue));
        property.getPropertyChangeEvent().fire(new PropertyChange<T>(oldValue, newValue));
    }
    
    public Property getProperty(String name){
        return this.properties.get(name);
    }
   
    /**
     * @return the parent
     */
    protected Thing getParent() {
        return parent;
    }
    /**
     * @param parent the parent to set
     */
    protected void setParent(Thing parent) {
        this.parent = parent;
    }
}
