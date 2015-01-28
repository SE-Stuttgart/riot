package de.uni_stuttgart.riot.thing.commons.event;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * TODO .
 *
 * @param <T>
 */
public abstract class Event<T extends EventInstance> {

    private Collection<EventListener<T>> eventListeners;

    /**
     * Constructor.
     */
    public Event() {
        this.eventListeners = new ArrayList<EventListener<T>>();
    }

    /**
     * registers event listener.
     * 
     * @param eventListener
     *            the event listener.
     */
    public void register(EventListener<T> eventListener) {
        this.eventListeners.add(eventListener);
    }

    /**
     * unregisters event listener.
     * 
     * @param eventListener
     *            the event listener to unregister.
     */
    public void unregister(EventListener<T> eventListener) {
        boolean result = this.eventListeners.remove(eventListener);
        if (!result) {
            System.out.println("may throw exception (Event)"); // FIXME may throw exception
        }
    }

    /**
     * notifies listener about the given event.
     * 
     * @param event
     *            fired event.
     */
    protected void notifyListeners(T event) {
        for (EventListener<T> eventListener : eventListeners) {
            eventListener.onFired(event);
        }
    }

    /**
     * fires the given event.
     * 
     * @param event
     *            event to be fired.
     */
    public void fire(T event) {
        this.notifyListeners(event);
    }

    /**
     * creates an instance.
     * 
     * @param params
     *            collection of properties.
     * @return the created instance.
     * @throws Exception .
     */
    public abstract T createInstance(Collection<Property> params) throws Exception;
}
