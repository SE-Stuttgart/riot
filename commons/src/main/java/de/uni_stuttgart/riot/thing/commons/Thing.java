package de.uni_stuttgart.riot.thing.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.Event;

public abstract class Thing extends Storable{

    private transient Map<String,Property> properties;
    private transient Map<String,Event> events;
    private transient Collection<Action> actions;
    private final String name;
    
    public Thing(String name) {
        this.name = name;
    }
    
    //TODO geht das so
    public boolean hasProperty(Property property){
        for (Property p : this.properties.values()) {
            if(p.getName().equals(property.getName()) 
                    && p.getValue().getClass().equals(property.getValue().getClass())){
                return true;
            }
        }
        return false;
    }
    
    public void addAction(Action action){
        if(this.actions == null) this.actions = new ArrayList<Action>();
        this.actions.add(action);
    }
    
    public <T> void addProperty(Property<T> property){
        if(this.properties == null) this.properties = new HashMap<String, Property>();
        this.properties.put(property.getName(),property);
    }

    public String getName() {
        return name;
    }
    
    public Map<String,Property> getProperties(){
        if(this.properties == null) this.properties = new HashMap<String, Property>();
        return Collections.unmodifiableMap(this.properties);
    }
    
    public Collection<Action> getActions(){
        if(this.actions == null) this.actions = new ArrayList<Action>();
        return Collections.unmodifiableCollection(this.actions);
    }
    
}
