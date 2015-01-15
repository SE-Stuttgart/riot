package de.uni_stuttgart.riot.thing.event;

public class EventListener<T extends FiredEvent> {
    
    public void onFired(T event){
        System.out.println("Event happend");
    }

}
