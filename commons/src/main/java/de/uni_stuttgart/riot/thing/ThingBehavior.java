package de.uni_stuttgart.riot.thing;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.ReferenceResolver;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A ThingBehavior instance is assigned to exactly one {@link Thing} instance and controls what needs to be done when an action or event is
 * fired, etc.
 * 
 * @author Philipp Keck
 */
public abstract class ThingBehavior implements ReferenceResolver {

    /*
     * -------------------------- General fields and methods
     */

    private Thing thing;
    private final DelegatingReferenceResolver resolver;

    /**
     * Creates a new ThingBehavior. Uses an empty {@link 
     */
    public ThingBehavior() {
        this.resolver = new DelegatingReferenceResolver();
    }

    /**
     * Creates a new ThingBehavior.
     * 
     * @param resolver
     *            The reference resolver to be used by this behavior.
     */
    public ThingBehavior(DelegatingReferenceResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("resolver must not be null!");
        }
        this.resolver = resolver;
    }

    /**
     * Registers the behavior with its thing. This method may only be called once!
     * 
     * @param newThing
     *            The thing to register with.
     */
    protected synchronized void register(Thing newThing) {
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

    /**
     * Gets the reference resolver used by this behavior.
     * 
     * @return The resolver.
     */
    protected DelegatingReferenceResolver getDelegatingResolver() {
        return resolver;
    }

    /**
     * Resolves an entity reference. Note that calling this method is usually expensive and might involve a number of server and/or database
     * queries.
     * 
     * @param <R>
     *            The expected type of the target entity.
     * @param reference
     *            The reference to be resolved.
     * @param targetType
     *            The expected type of the target entity.
     * @return The resolved reference. This method returns <tt>null</tt> if and only if <tt>reference.</tt>{@link Reference#getId()} was
     *         <tt>null</tt>.
     * @throws ResolveReferenceException
     *             When resolving the reference fails. See the subclasses of {@link ResolveReferenceException} for details on possible
     *             causes.
     */
    public <R extends Referenceable<? super R>> R resolve(Reference<R> reference, Class<R> targetType) throws ResolveReferenceException {
        return resolve(reference.getId(), targetType);
    }

    @Override
    public <R extends Referenceable<? super R>> R resolve(Long targetId, Class<R> targetType) throws ResolveReferenceException {
        return resolver.resolve(targetId, targetType);
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

    /*
     * -------------------------- Creational methods (Called by the Thing to set itself up)
     */

    /**
     * Creates a new action for the thing.
     * 
     * @param <A>
     *            The type of instances of the action.
     * @param actionName
     *            The name of the action. Should check for uniqueness.
     * @param instanceType
     *            The type of instances of the action.
     * @return The newly created action.
     */
    protected <A extends ActionInstance> Action<A> newAction(String actionName, Class<A> instanceType) {
        if (actionName == null || actionName.isEmpty()) {
            throw new IllegalArgumentException("actionName must not be empty!");
        } else if (thing.actions.containsKey(actionName)) {
            throw new IllegalArgumentException("Duplicate action " + actionName);
        }
        if (instanceType == null) {
            throw new IllegalArgumentException("instanceType must not be null!");
        }

        Action<A> action = new Action<A>(thing, actionName, instanceType);
        thing.actions.put(actionName, action);
        return action;
    }

    /**
     * Creates a new event for the thing.
     * 
     * @param <E>
     *            The type of instances of the event.
     * @param eventName
     *            The name of the event. Should check for uniqueness.
     * @param instanceType
     *            The type of instances of the event.
     * @return The newly created event.
     */
    protected <E extends EventInstance> Event<E> newEvent(String eventName, Class<E> instanceType) {
        checkEventArguments(eventName, instanceType);

        Event<E> event = new Event<E>(thing, eventName, instanceType);
        thing.events.put(eventName, event);
        return event;
    }

    /**
     * New a new notification for the thing.
     *
     * @param <E>
     *            The type of instances of the event.
     * @param notificationName
     *            The name of the event. Should check for uniqueness.
     * @param instanceType
     *            The name of the event. Should check for uniqueness.
     * @return The newly created notification.
     */
    protected <E extends EventInstance> NotificationEvent<E> newNotification(String notificationName, Class<E> instanceType) {
        checkEventArguments(notificationName, instanceType);

        NotificationEvent<E> notification = new NotificationEvent<E>(thing, notificationName, instanceType);
        thing.events.put(notificationName, notification);
        return notification;
    }

    /**
     * Creates a new property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Should check for uniqueness.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property (may be null).
     * @param uiHint
     *            The UI hint for the property (may be null).
     * @return The newly created property.
     */
    protected <V> Property<V> newProperty(String propertyName, Class<V> valueType, V initialValue, UIHint uiHint) {
        checkPropertyArguments(propertyName, valueType);
        return addPropertyInternal(new Property<V>(thing, propertyName, valueType, initialValue, uiHint));
    }

    /**
     * Creates a new writable property for the thing.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param propertyName
     *            The name of the property. Should check for uniqueness.
     * @param valueType
     *            The type of the property's values.
     * @param initialValue
     *            The initial value of the property (may be null).
     * @param uiHint
     *            The UI hint for the property (may be null).
     * @return The newly created property.
     */
    protected <V> WritableProperty<V> newWritableProperty(String propertyName, Class<V> valueType, V initialValue, UIHint uiHint) {
        checkPropertyArguments(propertyName, valueType);
        return addPropertyInternal(new WritableProperty<V>(thing, propertyName, valueType, initialValue, uiHint));
    }

    /**
     * Creates a new reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property. Should check for uniqueness.
     * @param targetType
     *            The type of the referenced entities.
     * @param uiHint
     *            The UI hint for the property (may be null).
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> ReferenceProperty<R> newReferenceProperty(String propertyName, Class<R> targetType, UIHint uiHint) {
        checkPropertyArguments(propertyName, targetType);
        return addPropertyInternal(new ReferenceProperty<R>(thing, propertyName, targetType, uiHint));
    }

    /**
     * Creates a new writable reference property for the thing.
     * 
     * @param <R>
     *            The type of the referenced entities.
     * @param propertyName
     *            The name of the property. Should check for uniqueness.
     * @param targetType
     *            The type of the referenced entities.
     * @param uiHint
     *            The UI hint for the property (may be null).
     * @return The newly created property.
     */
    protected <R extends Referenceable<? super R>> WritableReferenceProperty<R> newWritableReferenceProperty(String propertyName, Class<R> targetType, UIHint uiHint) {
        checkPropertyArguments(propertyName, targetType);
        return addPropertyInternal(new WritableReferenceProperty<R>(thing, propertyName, targetType, uiHint));
    }

    /**
     * Helper method to check arguments for property creators.
     * 
     * @param propertyName
     *            The name of the property.
     * @param valueType
     *            The type of the property's values.
     */
    private <V> void checkPropertyArguments(String propertyName, Class<V> valueType) {
        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("propertyName must not be empty!");
        } else if (thing.properties.containsKey(propertyName)) {
            throw new IllegalArgumentException("Duplicate property " + propertyName);
        }
        if (valueType == null) {
            throw new IllegalArgumentException("valueType must not be null!");
        }
    }

    private <E> void checkEventArguments(String eventName, Class<E> instanceType) {
        if (eventName == null || eventName.isEmpty()) {
            throw new IllegalArgumentException("eventName must not be empty!");
        } else if (thing.events.containsKey(eventName)) {
            throw new IllegalArgumentException("Duplicate event " + eventName);
        }
        if (instanceType == null) {
            throw new IllegalArgumentException("instanceType must not be null!");
        }
    }

    /**
     * A helper method to add a newly created property.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param <P>
     *            The type of the {@link Property} object itself.
     * @param property
     *            The property to be added.
     */
    private <V, P extends Property<V>> P addPropertyInternal(P property) {
        if (thing.events.containsKey(property.getChangeEvent().getName())) {
            throw new IllegalArgumentException("Duplicate property change event " + property.getChangeEvent().getName());
        }

        if (property instanceof WritableProperty) {
            WritableProperty<V> writableProperty = (WritableProperty<V>) property;
            if (thing.actions.containsKey(writableProperty.getSetAction().getName())) {
                throw new IllegalArgumentException("Duplicate property set action " + writableProperty.getName());
            }
            thing.actions.put(writableProperty.getSetAction().getName(), writableProperty.getSetAction());
        }

        thing.properties.put(property.getName(), property);
        thing.events.put(property.getChangeEvent().getName(), property.getChangeEvent());

        return property;
    }

}
