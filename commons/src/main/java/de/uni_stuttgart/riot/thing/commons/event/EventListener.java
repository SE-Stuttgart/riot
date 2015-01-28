package de.uni_stuttgart.riot.thing.commons.event;

/**
 * The Event Listener.
 *
 * @param <T>
 */
public abstract class EventListener<T extends EventInstance> {

    /**
     * this method is called when the event is fired.
     * 
     * @param event
     *            fired event.
     */
    public abstract void onFired(T event);

}
