package de.uni_stuttgart.riot.thing.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.event.Event;

/**
 * A {@link Thing} (e.g. Car, House, ...) contains Properties, {@link Action}s and {@link Action}s.
 *
 */
public abstract class Thing extends Storable {

    private transient Map<String, Property> properties;
    private transient Collection<Event> events;
    private transient Collection<Action> actions;
    private String name;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Thing(String name) {
        this.name = name;
        this.actions = new ArrayList<Action>();
        this.setEvents(new ArrayList<Event>());
        this.properties = new HashMap<String, Property>();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Thing [properties=");
        builder.append(properties);
        builder.append(", events=");
        builder.append(events);
        builder.append(", actions=");
        builder.append(actions);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Constructor.
     */
    public Thing() {
        this.actions = new ArrayList<Action>();
        this.setEvents(new ArrayList<Event>());
        this.properties = new HashMap<String, Property>();
    }

    // TODO geht das so
    /**
     * checks if it has the given property.
     * 
     * @param property
     *            the property to check.
     * @return true, if it has the property, false otherwise.
     */
    public boolean hasProperty(Property property) {
        for (Property p : this.properties.values()) {
            if (p.getName().equals(property.getName()) && p.getValue().getClass().equals(property.getValue().getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * adds action to the {@link Thing}.
     * 
     * @param action
     *            {@link Action} to add.
     */
    public void addAction(Action action) {
        this.actions.add(action);
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }
    
    /**
     * adds property to the {@link Thing}.
     * 
     * @param property
     *            {@link Property} to add.
     * @param <T>
     *            property type.
     */
    public <T> void addProperty(Property<T> property) {
        this.properties.put(property.getName(), property);
    }

    public String getName() {
        return name;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public void setActions(Collection<Action> actions) {
        this.actions = actions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }
}
