package de.uni_stuttgart.riot.thing;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an Event that could be fired from a {@link Thing}.
 * 
 * @param <E>
 *            The type of the event's instances.
 */
public class Event<E extends EventInstance> {

    private final transient Thing thing;
    private final transient List<EventListener<? super E>> eventListeners = new ArrayList<EventListener<? super E>>();
    private final String name;
    private final Class<E> instanceType;

    /**
     * Creates a new event. Note that the constructor is internal. Things are required to instantiate their events through
     * {@link Thing#newEvent(String, Class)}.
     * 
     * @param thing
     *            The thing that this event belongs to.
     * @param name
     *            The name of the event.
     * @param instanceType
     *            The type of instances that will be fired by this event.
     */
    Event(Thing thing, String name, Class<E> instanceType) {
        this.thing = thing;
        this.name = name;
        this.instanceType = instanceType;
    }

    /**
     * Gets the containing thing.
     * 
     * @return The thing that this event belongs to.
     */
    public Thing getThing() {
        return thing;
    }

    /**
     * Gets the name.
     * 
     * @return The name of this event.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type.
     * 
     * @return The type of instances that will be fired by this event.
     */
    public Class<E> getInstanceType() {
        return instanceType;
    }

    /**
     * Registers an event listener.
     * 
     * @param eventListener
     *            The event listener.
     */
    public synchronized void register(EventListener<? super E> eventListener) {
        boolean wasFirst = this.eventListeners.isEmpty();
        this.eventListeners.add(eventListener);
        getThing().getBehavior().listenerAdded(this, eventListener, wasFirst);
    }

    /**
     * Unregisters an event listener.
     * 
     * @param eventListener
     *            The event listener to unregister.
     */
    public synchronized void unregister(EventListener<? super E> eventListener) {
        this.eventListeners.remove(eventListener);
        getThing().getBehavior().listenerRemoved(this, eventListener, this.eventListeners.isEmpty());
    }

    /**
     * Notifies listeners about the event instance.
     * 
     * @param eventInstance
     *            The fired event instance.
     */
    @SuppressWarnings("unchecked")
    protected void notifyListeners(E eventInstance) {
        // Note: It is important to create a copy of the listeners here and it is also important that this method will never be
        // synchronized. It must be possible for a listener to unregister itself while it is being notified.
        for (EventListener<? super E> eventListener : eventListeners.toArray(new EventListener[eventListeners.size()])) {
            eventListener.onFired(this, eventInstance);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event<?> other = (Event<?>) obj;
        if (instanceType == null) {
            if (other.instanceType != null) {
                return false;
            }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
