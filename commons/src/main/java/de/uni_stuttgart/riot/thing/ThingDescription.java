package de.uni_stuttgart.riot.thing;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to describe the structure of a thing.
 * 
 * @author Philipp Keck
 */
public class ThingDescription {

    private final Class<? extends Thing> type;
    private final List<EventDescription> events;
    private final List<ActionDescription> actions;
    private final List<PropertyDescription> properties;

    /**
     * Creates a new thing description.
     * 
     * @param type
     *            The type of the thing.
     * @param events
     *            The event descriptions for the thing.
     * @param actions
     *            The action descriptions for the thing.
     * @param properties
     *            The property descriptions for the thing.
     */
    @JsonCreator
    private ThingDescription(@JsonProperty("type") Class<? extends Thing> type, @JsonProperty("events") List<EventDescription> events, @JsonProperty("actions") List<ActionDescription> actions, @JsonProperty("properties") List<PropertyDescription> properties) {
        this.type = type;
        this.events = events;
        this.actions = actions;
        this.properties = properties;
    }

    /**
     * Gets the type of the thing.
     * 
     * @return The type of the thing.
     */
    public Class<? extends Thing> getType() {
        return type;
    }

    /**
     * Gets the event descriptions.
     * 
     * @return The event descriptions of the thing.
     */
    public List<EventDescription> getEvents() {
        return events;
    }

    /**
     * Finds an event description by the event name.
     * 
     * @param name
     *            The name of the event.
     * @return The event description.
     */
    public EventDescription getEventByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (EventDescription eventDescription : events) {
            if (eventDescription.getName().equals(name)) {
                return eventDescription;
            }
        }
        return null;
    }

    /**
     * Gets the action descriptions.
     * 
     * @return The action descriptions of the thing.
     */
    public List<ActionDescription> getActions() {
        return actions;
    }

    /**
     * Finds an action description by the action name.
     * 
     * @param name
     *            The name of the action.
     * @return The action description.
     */
    public ActionDescription getActionByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (ActionDescription actionDescription : actions) {
            if (actionDescription.getName().equals(name)) {
                return actionDescription;
            }
        }
        return null;
    }

    /**
     * Gets the property descriptions.
     * 
     * @return The property descriptions of the thing.
     */
    public List<PropertyDescription> getProperties() {
        return properties;
    }

    /**
     * Finds a property description by the property name.
     * 
     * @param name
     *            The name of the property.
     * @return The property description.
     */
    public PropertyDescription getPropertyByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (PropertyDescription propertyDescription : properties) {
            if (propertyDescription.getName().equals(name)) {
                return propertyDescription;
            }
        }
        return null;
    }

    /**
     * Creates a Thing description from the given thing.
     * 
     * @param thing
     *            The thing to describe.
     * @return The thing description.
     */
    public static ThingDescription create(Thing thing) {
        if (thing == null) {
            throw new IllegalArgumentException("thing must not be null!");
        }

        List<EventDescription> events = new ArrayList<EventDescription>();
        for (Event<?> event : thing.events.values()) {
            if (!(event instanceof PropertyChangeEvent)) {
                events.add(EventDescription.create(event));
            }
        }

        List<ActionDescription> actions = new ArrayList<ActionDescription>();
        for (Action<?> action : thing.actions.values()) {
            if (!(action instanceof PropertySetAction)) {
                actions.add(ActionDescription.create(action));
            }
        }

        List<PropertyDescription> properties = new ArrayList<PropertyDescription>();
        for (Property<?> property : thing.properties.values()) {
            properties.add(PropertyDescription.create(property));
        }

        return new ThingDescription(thing.getClass(), events, actions, properties);
    }
}
