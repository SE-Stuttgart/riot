package de.uni_stuttgart.riot.thing.house.roller_shutter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Action to adjust the position of a shutter. 
 *
 */
public class AdjustShutterPostion extends ActionInstance{

    @Parameter(ui = UIHint.PercentageSlider.class)
    private final double position;

    /**
     * Creates a new AdjustShutterPostion action instance.
     * 
     * @param action
     *            The action.
     * @param position
     *            The position of the shutter
     */
    public AdjustShutterPostion(Action<? extends ActionInstance> action, double position) {
        super(action);
        this.position = position;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public AdjustShutterPostion(JsonNode node) {
        super(node);
        this.position = node.get("position").asDouble();
    }

    /**
     * Gets the position.
     * 
     * @return position of the shutter
     */
    public double getPosition() {
        return position;
    }
}
