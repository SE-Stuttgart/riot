package de.uni_stuttgart.riot.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Base class for instances that are assigned to a specific property/event/action of a thing.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "type")
public abstract class BaseInstance {

    private final long thingId;
    private final String name;
    private final Date time;

    /**
     * Creates a new instance.
     * 
     * @param thingId
     *            The ID of the thing that owns the event/action that this instance belongs to.
     * @param name
     *            The name of the item (event or action).
     * @param time
     *            The time when this action/event was fired.
     */
    public BaseInstance(long thingId, String name, Date time) {
        this.thingId = thingId;
        this.name = name;
        this.time = time;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node.
     */
    @JsonCreator
    public BaseInstance(JsonNode node) {
        this.thingId = node.get("thingId").asLong();
        this.name = node.get("name").asText();
        this.time = new Date(node.get("time").asLong());
    }

    /**
     * Gets the thing ID.
     * 
     * @return The ID of the thing that owns the event/action that this instance belongs to.
     */
    public long getThingId() {
        return thingId;
    }

    /**
     * Gets the name.
     * 
     * @return The name of the item (event or action).
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the time.
     * 
     * @return The time when this action/event was fired.
     */
    public Date getTime() {
        return time;
    }

}
