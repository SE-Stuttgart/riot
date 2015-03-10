package de.uni_stuttgart.riot.thing.car;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.InstanceParameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * ActionInstance to refuel a tank with gasoline.
 *
 */
public class Refuel extends ActionInstance {

    /**
     * The gasoline amount in liter
     */
    @InstanceParameter(ui = UIHint.FractionalSlider.class, min = 0, max = Car.TANK_MAX_FILL_LEVEL)
    private final Double amount;

    /**
     * Creates a new Refuel action instance.
     * 
     * @param action
     *            The action.
     * @param amount
     *            Liter of gasoline.
     */
    public Refuel(Action<? extends ActionInstance> action, Double amount) {
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
    public Refuel(JsonNode node) {
        super(node);
        this.amount = node.get("amount").asDouble();
    }

    /**
     * Gets the amount.
     * 
     * @return Liter of gasoline.
     */
    public Double getAmount() {
        return amount;
    }

}
