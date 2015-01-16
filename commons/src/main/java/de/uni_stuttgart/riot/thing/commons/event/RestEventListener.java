package de.uni_stuttgart.riot.thing.commons.event;

public class RestEventListener<T extends EventInstance> extends EventListener<T>{

    @Override
    public void onFired(T event) {
        System.out.println(event + " TODO send via rest");
    }

}
