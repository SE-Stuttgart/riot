package de.uni_stuttgart.riot.thing.commons;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.action.Action;

public class ThingInfo {
    
    private String name;
    private Collection<Property> properties;
    private Collection<Action> actions;
    
    public ThingInfo(String name, Collection<Property> properties, Collection<Action> actions) {
        this.actions = actions;
        this.properties = properties;
        this.name = name;
    }
    
    public ThingInfo() {
    }

    public String getName() {
        return name;
    }

    public Collection<Property> getProperties() {
        return properties;
    }

    public Collection<Action> getActions() {
        return actions;
    }

}
