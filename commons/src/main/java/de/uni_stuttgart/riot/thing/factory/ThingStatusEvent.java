package de.uni_stuttgart.riot.thing.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;

/**
 * An event instance to signal that the status of a factory thing (machine, robot..) changed.
 */
public class ThingStatusEvent extends EventInstance {

    /** The status. */
    private final String status;

    /**
     * Instantiates a new thing status event.
     *
     * @param event
     *            the event
     * @param status
     *            the status
     */
    public ThingStatusEvent(Event<? extends EventInstance> event, String status) {
        super(event);
        this.status = status;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public ThingStatusEvent(JsonNode node) {
        super(node);
        this.status = node.get("status").toString();
    }

    /**
     * Gets the status.
     *
     * @return the actual status of the thing.
     */
    public String getStatus() {
        return this.status;
    }
}
