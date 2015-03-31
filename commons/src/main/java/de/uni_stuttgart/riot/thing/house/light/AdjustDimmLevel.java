package de.uni_stuttgart.riot.thing.house.light;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Action to adjust the dimm level of a dimmable light.
 *
 */
public class AdjustDimmLevel extends ActionInstance {

    @Parameter(ui = UIHint.PercentageSlider.class)
    private final double level;

    /**
     * Creates a new AdjustDimmLevel action instance.
     * 
     * @param action
     *            The action.
     * @param level
     *            dimm level
     */
    public AdjustDimmLevel(Action<? extends ActionInstance> action, double level) {
        super(action);
        this.level = level;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public AdjustDimmLevel(JsonNode node) {
        super(node);
        this.level = node.get("level").asDouble();
    }

    /**
     * Gets the dimm level.
     * 
     * @return dimm level
     */
    public double getLevel() {
        return level;
    }
}
