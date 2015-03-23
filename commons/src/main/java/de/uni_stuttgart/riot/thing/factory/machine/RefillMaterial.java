package de.uni_stuttgart.riot.thing.factory.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An action to refill the material tank with additional material pieces.
 */
public class RefillMaterial extends ActionInstance {

    @Parameter(ui = UIHint.IntegralSlider.class, min = 0, max = Machine.MATERIAL_TANK_SIZE)
    private final int amount;

    /**
     * Creates a new RefillMaterial action instance.
     * 
     * @param action
     *            The action.
     * @param amount
     *            The number of beans to refill.
     */
    public RefillMaterial(Action<? extends ActionInstance> action, int amount) {
        super(action);
        this.amount = amount;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public RefillMaterial(JsonNode node) {
        super(node);
        this.amount = node.get("amount").asInt();
    }

    /**
     * Gets the amount.
     * 
     * @return The number of beans to refill.
     */
    public int getAmount() {
        return amount;
    }

}
