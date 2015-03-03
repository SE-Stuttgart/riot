package de.uni_stuttgart.riot.thing;

import java.beans.PropertyChangeEvent;
import java.util.Map;

/**
 * A ThingBehavior instance is assigned to exactly one {@link Thing} instance and controls what needs to be done when an action or event is
 * fired, etc.
 * 
 * @author Philipp Keck
 */
public abstract class ThingBehavior {

    /*
     * -------------------------- General fields and methods
     */

    private Thing thing;

    /**
     * Registers the behavior with its thing. This method may only be called once!
     * 
     * @param newThing
     *            The thing to register with.
     */
    synchronized void register(Thing newThing) {
        if (this.thing != null) {
            throw new IllegalStateException("This behavior was already register with " + this.thing);
        }
        this.thing = newThing;
    }

    /**
     * Gets the thing.
     * 
     * @return The thing that this behavior is responsible for.
     */
    public Thing getThing() {
        return thing;
    }

    /*
     * -------------------------- Helper methods for behavior implementations
     */

    /**
     * Retrieves the thing's events.
     * 
     * @return All events of the thing. Note: Modifications to this map should be done with care! This map should not be passed outside of
     *         the behavior.
     */
    protected Map<String, Event<?>> getEvents() {
        return getThing().events;
    }

    /**
     * Retrieves the thing's actions.
     * 
     * @return All actions of the thing. Note: Modifications to this map should be done with care! This map should not be passed outside of
     *         the behavior.
     */
    protected Map<String, Action<?>> getActions() {
        return getThing().actions;
    }

    /**
     * Retrieves the thing's properties.
     * 
     * @return All properties of the thing. Note: Modifications to this map should be done with care! This map should not be passed outside
     *         of the behavior.
     */
    protected Map<String, Property<?>> getProperties() {
        return getThing().properties;
    }

    /**
     * Finds an action from the instance.
     * 
     * @param <A>
     *            The type of the action instance.
     * @param actionInstance
     *            The action instance.
     * @return The corresponding action.
     */
    protected <A extends ActionInstance> Action<A> getActionFromInstance(A actionInstance) {
        if (actionInstance == null) {
            throw new IllegalArgumentException("actionInstance must not be null!");
        } else if (thing.getId() != actionInstance.getThingId()) {
            throw new IllegalArgumentException("The action does not belong to this thing " + thing.getId() + " but to " + actionInstance.getThingId());
        }

        @SuppressWarnings("unchecked")
        Action<A> action = getThing().getAction(actionInstance.getName(), (Class<A>) actionInstance.getClass());
        if (action == null) {
            throw new IllegalArgumentException("The thing " + getThing() + " does not have the action " + actionInstance.getName());
        }
        return action;
    }

    /**
     * Finds an event from the instance.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param eventInstance
     *            The event instance.
     * @return The corresponding event.
     */
    protected <E extends EventInstance> Event<E> getEventFromInstance(E eventInstance) {
        if (eventInstance == null) {
            throw new IllegalArgumentException("eventInstance must not be null!");
        } else if (thing.getId() != eventInstance.getThingId()) {
            throw new IllegalArgumentException("The event does not belong to this thing " + thing.getId() + " but to " + eventInstance.getThingId());
        }

        @SuppressWarnings("unchecked")
        Event<E> event = getThing().getEvent(eventInstance.getName(), (Class<E>) eventInstance.getClass());
        if (event == null) {
            throw new IllegalArgumentException("The thing " + getThing() + " does not have the event " + eventInstance.getName());
        }
        return event;
    }

    /**
     * Notifies the (local!) listeners of the given event. This method should be called to propagate the event to local handlers. Note that
     * for {@link PropertyChangeEvent}s this will also change the property's value.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param event
     *            The event.
     * @param eventInstance
     *            The event instance.
     */
    protected <E extends EventInstance> void notifyListeners(Event<E> event, E eventInstance) {
        event.notifyListeners(eventInstance);
    }

    /**
     * Silently (without raising any events) sets the value of the given property. Use with care!
     * 
     * @param <V>
     *            The type of the property's value.
     * @param property
     *            The property to be set.
     * @param value
     *            The new value for the property.
     */
    protected static <V> void silentSetThingProperty(Property<V> property, V value) {
        property.setValueSilently(value);
    }

    /*
     * -------------------------- Behavioral methods (Called by the Thing, by the User)
     */

    /**
     * Called when the user fired an action on this thing, i.e., when {@link Action#fire(ActionInstance)} was called (on this computer or on
     * another one).
     * 
     * @param <A>
     *            The type of the action instance.
     * @param actionInstance
     *            The action instance.
     */
    protected abstract <A extends ActionInstance> void userFiredAction(A actionInstance);

    /**
     * Called when the user modified a property, i.e., when the user called {@link WritableProperty#set(Object)}. The default implementation
     * simply fires the {@link WritableProperty#getSetAction()} of the property, which will at some point result in the value being changed
     * and reported to this computer through respective property change events.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param property
     *            The property.
     * @param newValue
     *            The new value for the property.
     */
    protected <V> void userModifiedProperty(WritableProperty<V> property, V newValue) {
        property.getSetAction().fire(newValue);
    }

    /**
     * Called when an event listener has been added to an event. The thing behavior can use this to actually start firing the event in the
     * first place. When there is no listener attached, there is no need to fire it, and also no need to determine when to fire it, which
     * might be expensive. The default implementation does nothing.
     * 
     * @param <E>
     *            The type of the event instances.
     * @param event
     *            The event.
     * @param listener
     *            The new listener.
     * @param wasFirst
     *            True if the listener was the first one, i.e., if previously there were no listeners.
     */
    protected <E extends EventInstance> void listenerAdded(Event<E> event, EventListener<? super E> listener, boolean wasFirst) {
    }

    /**
     * Called when an event listener has been removed from an event. This is the counterpart of
     * {@link #listenerRemoved(Event, EventListener, boolean)}. The default implementation does nothing.
     * 
     * @param <E>
     *            The type of the event instances.
     * @param event
     *            The event.
     * @param listener
     *            The removed listener.
     * @param wasLast
     *            True if the listener was the last one, i.e., if there are no listeners anymore.
     */
    protected <E extends EventInstance> void listenerRemoved(Event<E> event, EventListener<? super E> listener, boolean wasLast) {
    }

}
