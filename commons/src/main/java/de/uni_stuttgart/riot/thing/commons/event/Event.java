package de.uni_stuttgart.riot.thing.commons.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representation of an Event that could be fired from a thing.
 * 
 * @param <T>
 */
@JsonIgnoreProperties({ "eventListeners" })
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
     * @param thingId
     *            the event listener to unregister.
     */
    public void unregister(final long thingId) {
        for (Iterator<EventListener<T>> it = this.eventListeners.iterator(); it.hasNext(); /* */) {
            if (it.next().getThingId() == thingId) {
                it.remove();
            }
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
     * Returns true if and only if the given instance is of this type.
     * 
     * @param eventInstance
     *            the instance
     * @return true if so, false otherwise
     */
    public abstract boolean isTypeOf(EventInstance eventInstance);

}
