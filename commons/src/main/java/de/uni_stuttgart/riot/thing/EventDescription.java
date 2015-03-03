package de.uni_stuttgart.riot.thing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to describe the structure of an event.
 * 
 * @author Philipp Keck
 */
public class EventDescription {

    private final String name;
    private final BaseInstanceDescription instanceDescription;

    /**
     * Instantiates a new EventDescription. Use {@link #create(Event)} to retrieve an instance.
     * 
     * @param name
     *            The name of the event.
     * @param instanceDescription
     *            A description of the expected instances.
     */
    @JsonCreator
    private EventDescription(@JsonProperty("name") String name, @JsonProperty("instanceDescription") BaseInstanceDescription instanceDescription) {
        this.name = name;
        this.instanceDescription = instanceDescription;
    }

    /**
     * Gets the name.
     * 
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the instance description.
     * 
     * @return A description of the expected instances.
     */
    public BaseInstanceDescription getInstanceDescription() {
        return instanceDescription;
    }

    /**
     * Creates a new event description from the given event.
     * 
     * @param event
     *            The event to describe.
     * @return The description of the event.
     */
    public static EventDescription create(Event<?> event) {
        return new EventDescription(event.getName(), BaseInstanceDescription.create(event.getInstanceType()));
    }

}
