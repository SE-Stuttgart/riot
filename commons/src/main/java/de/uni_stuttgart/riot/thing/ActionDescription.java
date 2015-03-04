package de.uni_stuttgart.riot.thing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to describe the structure of an action.
 * 
 * @author Philipp Keck
 */
public class ActionDescription {

    private final String name;
    private final BaseInstanceDescription instanceDescription;

    /**
     * Instantiates a new ActionDescription. Use {@link #create(Action)} to retrieve an instance.
     * 
     * @param name
     *            The name of the action.
     * @param instanceDescription
     *            A description of the expected instances.
     */
    @JsonCreator
    private ActionDescription(@JsonProperty("name") String name, @JsonProperty("instanceDescription") BaseInstanceDescription instanceDescription) {
        this.name = name;
        this.instanceDescription = instanceDescription;
    }

    /**
     * Gets the name.
     * 
     * @return The name of the action.
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
     * Creates a new action description from the given action.
     * 
     * @param action
     *            The action to describe.
     * @return The description of the action.
     */
    public static ActionDescription create(Action<?> action) {
        return new ActionDescription(action.getName(), BaseInstanceDescription.create(action.getInstanceType()));
    }

}
