package de.uni_stuttgart.riot.thing.commons;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.Event;

public abstract class Thing {

    private Collection<Property> properties;
    private Collection<Event> events;
    private Collection<Action> actions;
    
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
    
    protected <T> void addProperty(Property<T> property){
        this.addAction(new PropertySetAction<T>(property.getName(),this));
        this.properties.add(property);
    }
    
}
