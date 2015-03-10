package de.uni_stuttgart.riot.thing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A {@link Thing} (e.g. Car, House, ...) contains Properties, supported {@link Action}s and {@link Event}s.
 */
public class Thing extends Storable implements Referenceable<Thing> {

    final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
    final Map<String, Event<?>> events = new HashMap<String, Event<?>>();
    final Map<String, Action<?>> actions = new HashMap<String, Action<?>>();
    private String name;
    private long ownerId;
    private final transient ThingBehavior behavior;

    /**
     * Creates a new thing.
     * 
     * @param name
     *            The name of the thing.
     * @param behavior
     *            The behavior for this thing.
     */
    public Thing(String name, ThingBehavior behavior) {
        this.name = name;
        this.behavior = behavior;
        this.behavior.register(this);
    }

    /**
     * Creates a new action for the thing.
     * 
     * @param <A>
     *            The type of instances of the action.
     * @param actionName
     *            The name of the action. Must be unique, i.e., this method can only be called once for each name.
     * @param instanceType
     *            The type of instances of the action.
     * @return The newly created action.
     */
    protected <A extends ActionInstance> Action<A> newAction(String actionName, Class<A> instanceType) {
        return getBehavior().newAction(actionName, instanceType);
    }

    /**
     * Creates a new action without parameters for the thing.
     * 
     * @param actionName
     *            The name of the action. Must be unique, i.e., this method can only be called once for each name.
     * @return The newly created action.
     */
    protected Action<ActionInstance> newAction(String actionName) {
        return getBehavior().newAction(actionName, ActionInstance.class);
    }

    /**
     * Creates a new event for the thing.
     * 
     * @param <E>
     *            The type of instances of the event.
     * @param eventName
     *            The name of the event. Must be unique, i.e,. this method can only be called once for each name.
     * @param instanceType
     *            The type of instances of the event.
     * @return The newly created event.
     */
    protected <E extends EventInstance> Event<E> newEvent(String eventName, Class<E> instanceType) {
        return getBehavior().newEvent(eventName, instanceType);
    }

    /**
     * Creates a new event without parameters for the thing.
     * 
     * @param eventName
     *            The name of the event. Must be unique, i.e,. this method can only be called once for each name.
     * @return The newly created event.
     */
    protected Event<EventInstance> newEvent(String eventName) {
        return getBehavior().newEvent(eventName, EventInstance.class);
    }

    /**
     * Creates a new property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Must be unique, i.e,. this method can only be called once for each name.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property.
     * @return The newly created property.
     */
    protected <V> Property<V> newProperty(String propertyName, Class<V> valueType, V initialValue) {
        return newProperty(propertyName, valueType, initialValue, null);
    }

    /**
     * Creates a new property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Must be unique, i.e,. this method can only be called once for each name.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property.
     * @param uiHint
     *            The UI hint for the property. See {@link UIHint} for static factory methods.
     * @return The newly created property.
     */
    protected <V> Property<V> newProperty(String propertyName, Class<V> valueType, V initialValue, UIHint uiHint) {
        return getBehavior().newProperty(propertyName, valueType, initialValue, uiHint);
    }

    /**
     * Creates a new writable property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Must be unique, i.e,. this method can only be called once for each name.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property.
     * @return The newly created property.
     */
    protected <V> WritableProperty<V> newWritableProperty(String propertyName, Class<V> valueType, V initialValue) {
        return newWritableProperty(propertyName, valueType, initialValue, null);
    }

    /**
     * Creates a new writable property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Must be unique, i.e,. this method can only be called once for each name.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property.
     * @param uiHint
     *            The UI hint for the property. See {@link UIHint} for static factory methods.
     * @return The newly created property.
     */
    protected <V> WritableProperty<V> newWritableProperty(String propertyName, Class<V> valueType, V initialValue, UIHint uiHint) {
        return getBehavior().newWritableProperty(propertyName, valueType, initialValue, uiHint);
    }

    /**
     * Creates a new reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property.
     * @param targetType
     *            The type of the referenced entities.
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> ReferenceProperty<R> newReferenceProperty(String propertyName, Class<R> targetType) {
        return newReferenceProperty(propertyName, targetType, null);
    }

    /**
     * Creates a new reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property.
     * @param targetType
     *            The type of the referenced entities.
     * @param uiHint
     *            The UI hint for the property. See {@link UIHint} for static factory methods.
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> ReferenceProperty<R> newReferenceProperty(String propertyName, Class<R> targetType, UIHint uiHint) {
        return getBehavior().newReferenceProperty(propertyName, targetType, uiHint);
    }

    /**
     * Creates a new writable reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property.
     * @param targetType
     *            The type of the referenced entities.
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> WritableReferenceProperty<R> newWritableReferenceProperty(String propertyName, Class<R> targetType) {
        return newWritableReferenceProperty(propertyName, targetType, null);
    }

    /**
     * Creates a new writable reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property.
     * @param targetType
     *            The type of the referenced entities.
     * @param uiHint
     *            The UI hint for the property. See {@link UIHint} for static factory methods.
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> WritableReferenceProperty<R> newWritableReferenceProperty(String propertyName, Class<R> targetType, UIHint uiHint) {
        return getBehavior().newWritableReferenceProperty(propertyName, targetType, uiHint);
    }

    /**
     * Gets the thing's name.
     * 
     * @return The name of this thing.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of this thing. TODO Do we need this method?
     * 
     * @param name
     *            The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the owner ID.
     * 
     * @return The ID of the user that owns this thing.
     */
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the owner ID.
     * 
     * @param ownerId
     *            The ID of the user that owns this thing.
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the behavior.
     * 
     * @return The behavior of this thing.
     */
    @JsonIgnore
    public ThingBehavior getBehavior() {
        return behavior;
    }

    /**
     * Retrieves an event by its name.
     * 
     * @param eventName
     *            The name of the event.
     * @return The event or <tt>null</tt> if the event does not exist.
     */
    public Event<?> getEvent(String eventName) {
        return events.get(eventName);
    }

    /**
     * Retrieves a typed event by its name.
     * 
     * @param <E>
     *            The type of the event's instances.
     * @param eventName
     *            The name of the event.
     * @param instanceType
     *            The expected type of the event.
     * @return The event, <tt>null</tt> if an event with this name does not exist or an {@link IllegalArgumentException} if the event exists
     *         but has a different instance type.
     */
    public <E extends EventInstance> Event<E> getEvent(String eventName, Class<E> instanceType) {
        Event<?> event = getEvent(eventName);
        if (event == null) {
            return null;
        } else if (event.getInstanceType() != instanceType) {
            throw new IllegalArgumentException("The instanceType " + instanceType + " did not match the actual type " + event.getInstanceType());
        }

        @SuppressWarnings("unchecked")
        Event<E> result = (Event<E>) event;
        return result;
    }

    /**
     * Returns a non-editable collection of all events that this thing has.
     * 
     * @return The events of this thing.
     */
    public Collection<Event<?>> getEvents() {
        return Collections.unmodifiableCollection(events.values());
    }

    /**
     * Retrieves an action by its name.
     * 
     * @param actionName
     *            The name of the action.
     * @return The action or <tt>null</tt> if the action does not exist.
     */
    public Action<?> getAction(String actionName) {
        return actions.get(actionName);
    }

    /**
     * Retrieves a typed action by its name.
     * 
     * @param <A>
     *            The type of the action instances.
     * @param actionName
     *            The name of the action.
     * @param instanceType
     *            The expected type of the action.
     * @return The action, <tt>null</tt> if an action with this name does not exist or an {@link IllegalArgumentException} if the action
     *         exists but has a different instance type.
     */
    public <A extends ActionInstance> Action<A> getAction(String actionName, Class<A> instanceType) {
        Action<?> action = getAction(actionName);
        if (action == null) {
            return null;
        } else if (action.getInstanceType() != instanceType) {
            throw new IllegalArgumentException("The instanceType " + instanceType + " did not match the actual type " + action.getInstanceType());
        }

        @SuppressWarnings("unchecked")
        Action<A> result = (Action<A>) action;
        return result;
    }

    /**
     * Returns a non-editable collection of all actions that this thing has.
     * 
     * @return The actions of this thing.
     */
    public Collection<Action<?>> getActions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    /**
     * Retrieves a property by its name.
     * 
     * @param propertyName
     *            The name of the property.
     * @return The property or <tt>null</tt> if the action does not exist.
     */
    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Retrieves a typed property by its name.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property.
     * @param valueType
     *            The expected value type of the property.
     * @return The property, <tt>null</tt> if a property with this name does not exist or an {@link IllegalArgumentException} if the
     *         property exists but has a different value type.
     */
    public <V> Property<V> getProperty(String propertyName, Class<V> valueType) {
        Property<?> property = getProperty(propertyName);
        if (property == null) {
            return null;
        } else if (property.getValueType() != valueType) {
            throw new IllegalArgumentException("The valueType " + valueType + " did not match the actual type " + property.getValueType());
        }

        @SuppressWarnings("unchecked")
        Property<V> result = (Property<V>) property;
        return result;
    }

    /**
     * Retrieves a writable property by its name.
     * 
     * @param propertyName
     *            The name of the property.
     * @return The property, <tt>null</tt> if a property with this name does not exist or a {@link IllegalArgumentException} if the property
     *         is not writable.
     */
    public Property<?> getWritableProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        if (property instanceof WritableProperty) {
            return (WritableProperty<?>) property;
        } else {
            throw new IllegalArgumentException("The property " + propertyName + " is not writable!");
        }
    }

    /**
     * Retrieves a typed writable property by its name.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property.
     * @param valueType
     *            The expected value type of the property.
     * @return The property, <tt>null</tt> if a property with this name does not exist or an {@link IllegalArgumentException} if the
     *         property exists but has a different value type or if it is not writable.
     */
    public <V> Property<V> getWritableProperty(String propertyName, Class<V> valueType) {
        Property<V> property = getProperty(propertyName, valueType);
        if (property instanceof WritableProperty) {
            return (WritableProperty<V>) property;
        } else {
            throw new IllegalArgumentException("The property " + propertyName + " is not writable!");
        }
    }

    /**
     * Returns a non-editable collection of all properties that this thing has.
     * 
     * @return The properties of this thing.
     */
    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection(properties.values());
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(getClass().getSimpleName());
        output.append("[id=");
        output.append(getId());
        output.append(", name=");
        output.append(getName());
        output.append(", behavior=");
        output.append(behavior.getClass().getSimpleName());
        for (Property<?> property : properties.values()) {
            output.append(", ");
            output.append(property.getName());
            output.append("=");
            output.append(property.get());
        }
        output.append("]");
        return output.toString();
    }

    // CHECKSTYLE:OFF (Generated Code)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Thing other = (Thing) obj;
        if (actions == null) {
            if (other.actions != null) {
                return false;
            }
        } else if (!actions.equals(other.actions)) {
            return false;
        }
        if (events == null) {
            if (other.events != null) {
                return false;
            }
        } else if (!events.equals(other.events)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        return true;
    }

}
