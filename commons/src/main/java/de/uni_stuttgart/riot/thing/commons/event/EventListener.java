package de.uni_stuttgart.riot.thing.commons.event;

/**
 * The Event Listener.
 *
 * @param <T>
 */
public abstract class EventListener<T extends EventInstance> {

    private final long thingId;
    
    public EventListener(long thingId) {
        this.thingId = thingId;
    }
    /**
     * this method is called when the event is fired.
     * 
     * @param event
     *            fired event.
     */
    public abstract void onFired(T event);
  
    public long getThingId() {
        return thingId;
    }

}
