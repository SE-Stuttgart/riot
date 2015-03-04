package de.uni_stuttgart.riot.thing.house.coffeemachine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.InstanceParameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An action to refill the bean tank with additional beans.
 * 
 * @author Philipp Keck
 */
public class RefillBeans extends ActionInstance {

    @InstanceParameter(ui = UIHint.IntegralSlider.class, min = 0, max = CoffeeMachine.BEAN_TANK_SIZE)
    private final int amount;

    /**
     * Creates a new RefillBeans action instance.
     * 
     * @param action
     *            The action.
     * @param amount
     *            The number of beans to refill.
     */
    public RefillBeans(Action<? extends ActionInstance> action, int amount) {
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
    public RefillBeans(JsonNode node) {
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
