package de.uni_stuttgart.riot.thing.event;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Event<T extends FiredEvent> {

    private Collection<EventListener<T>> eventListeners;
    
    public Event() {
        this.eventListeners = new ArrayList<EventListener<T>>();
    }
    
    public void register(EventListener<T> eventListener){
        this.eventListeners.add(eventListener);
    }
    
    public void unregister(EventListener<T> eventListener){
        boolean result = this.eventListeners.remove(eventListener);
        if(!result) System.out.println("may throw exception (Event)");//FIXME may throw exception
    }
    
    protected void notifyListeners(T event) {
        for (EventListener<T> eventListener : eventListeners) {
            eventListener.onFired(event);
        }
    }
    
    public void fire(T event){
        this.notifyListeners(event);
    }
    
}
