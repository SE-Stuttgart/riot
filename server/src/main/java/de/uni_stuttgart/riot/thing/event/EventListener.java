package de.uni_stuttgart.riot.thing.event;

public abstract class EventListener<T extends FiredEvent> {
    
    public abstract void onFired(T event);

}
