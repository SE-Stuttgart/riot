package de.uni_stuttgart.riot.thing.commons;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.Event;

public abstract class Thing extends Storable{

    private final Collection<Property> properties;
    private final Collection<Event> events;
    private final Collection<Action> actions;
    private final String name;
    
    public Thing(String name) {
        this.name = name;
        this.properties = new ArrayList<Property>();
        this.events = new ArrayList<Event>();
        this.actions = new ArrayList<Action>();
    }
    
    //TODO geht das so
    public boolean hasProperty(Property property){
        for (Property p : this.properties) {
            if(p.getName().equals(property.getName()) 
                    && p.getValue().getClass().equals(property.getValue().getClass())){
                return true;
            }
        }
        return false;
    }
    
    protected void addAction(Action action){
        this.actions.add(action);
    }
    
    public <T> void addProperty(Property<T> property){
        this.addAction(new PropertySetAction<T>(property.getName(),this));
        this.properties.add(property);
    }
    
}
