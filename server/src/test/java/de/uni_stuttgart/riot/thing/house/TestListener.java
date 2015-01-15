package de.uni_stuttgart.riot.thing.house;

import de.uni_stuttgart.riot.thing.event.EventListener;
import de.uni_stuttgart.riot.thing.event.PropertyChange;

public class TestListener<T> extends EventListener<PropertyChange<T>>{

    @Override
    public void onFired(PropertyChange<T> event) {
        System.out.println(event);
    }

}
