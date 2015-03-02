package de.uni_stuttgart.riot.thing;

/**
 * An event listener that listens to events of a certain type. Note that an event listener can be attached to multiple events.
 *
 * @param <E>
 *            The type of events.
 */
public interface EventListener<E extends EventInstance> {

    /**
     * This method is called when an event is fired.
     * 
     * @param event
     *            The event.
     * @param eventInstance
     *            The event instance.
     */
    void onFired(Event<? extends E> event, E eventInstance);

}
