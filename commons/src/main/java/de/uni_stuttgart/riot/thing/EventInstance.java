package de.uni_stuttgart.riot.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The fired instance of an {@link Event}. Subclasses of this class may be created to carry additional information about the event being
 * fired.
 */
public class EventInstance extends BaseInstance {

    /**
     * Instantiates a new event instance. The time is set to now.
     *
     * @param event
     *            The event that was fired.
     */
    public EventInstance(Event<? extends EventInstance> event) {
        super(event.getThing().getId(), event.getName(), new Date());
        if (!event.getInstanceType().isAssignableFrom(this.getClass())) {
            throw new IllegalArgumentException("The type " + event.getInstanceType() + " of the given event does not match this EventInstance!");
        }
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node.
     */
    @JsonCreator
    public EventInstance(JsonNode node) {
        super(node);
    }

    /**
     * Creates a new instance.
     * 
     * @param thingId
     *            The ID of the thing that owns the event that this instance belongs to.
     * @param name
     *            The name of the event.
     * @param time
     *            The time when this event was fired.
     */
    EventInstance(long thingId, String name, Date time) {
        super(thingId, name, time);
    }

}
