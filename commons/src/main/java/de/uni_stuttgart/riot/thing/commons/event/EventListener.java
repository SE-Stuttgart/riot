package de.uni_stuttgart.riot.thing.commons.event;

public abstract class EventListener<T extends EventInstance> {
    
    public abstract void onFired(T event);

}
