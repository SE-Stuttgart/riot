package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;

public class EventDBObject extends Storable {


    private final String factoryString;

    /**
     * Constructor.
     * 
     * @param factoryString
     *            .
     */
    public EventDBObject(String factoryString) {
        this.factoryString = factoryString;
    }

    public String getFactoryString() {
        return factoryString;
    }

    /**
     * Returns a corresponding {@link Action} object.
     * 
     * @param owner
     *            the owner {@link Thing}
     * @return .
     */
    public Event getTheEvent(Thing owner) {
        return new PropertyChangeEvent<String>(); // FIXME ADD types (by visitor)
    }

}
