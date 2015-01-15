package de.uni_stuttgart.riot.thing;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.thing.action.Action;
import de.uni_stuttgart.riot.thing.event.Event;

public abstract class Thing {
    
    private Collection<Property> properties;
    private Collection<Action> actions;
    private Collection<Event> events;
    private Thing parent;
    
    public Thing() {
        this.setActions(new ArrayList<Action>());
        this.setEvents(new ArrayList<Event>());
        this.setProperties(new ArrayList<Property>());
    }
    
    /**
     * @return the properties
     */
    protected Collection<Property> getProperties() {
        return properties;
    }
    /**
     * @param properties the properties to set
     */
    protected void setProperties(Collection<Property> properties) {
        this.properties = properties;
    }
    /**
     * @return the actions
     */
    protected Collection<Action> getActions() {
        return actions;
    }
    /**
     * @param actions the actions to set
     */
    protected void setActions(Collection<Action> actions) {
        this.actions = actions;
    }
    /**
     * @return the events
     */
    protected Collection<Event> getEvents() {
        return events;
    }
    /**
     * @param events the events to set
     */
    protected void setEvents(Collection<Event> events) {
        this.events = events;
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
