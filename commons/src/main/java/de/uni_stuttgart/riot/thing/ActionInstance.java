package de.uni_stuttgart.riot.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The (to-be-)fired instance of an {@link Action}. Subclasses of this class may be created to carry additional information about an action
 * being fired.
 */
public class ActionInstance extends BaseInstance {

    /**
     * Instantiates a new action instance. The time is set to now.
     *
     * @param action
     *            The action that was fired.
     */
    public ActionInstance(Action<? extends ActionInstance> action) {
        super(action.getThing().getId(), action.getName(), new Date());
        if (!action.getInstanceType().isAssignableFrom(this.getClass())) {
            throw new IllegalArgumentException("The type " + action.getInstanceType() + " of the given action does not match this ActionInstance!");
        }
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node.
     */
    @JsonCreator
    public ActionInstance(JsonNode node) {
        super(node);
    }

    /**
     * Creates a new instance.
     * 
     * @param thingId
     *            The ID of the thing that owns the action that this instance belongs to.
     * @param name
     *            The name of the action.
     * @param time
     *            The time when this action was fired.
     */
    ActionInstance(long thingId, String name, Date time) {
        super(thingId, name, time);
    }

}
