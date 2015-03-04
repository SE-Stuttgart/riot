package de.uni_stuttgart.riot.thing.house.coffeemachine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.InstanceParameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An event instance to signal that there are only little or no beans left.
 * 
 * @author Philipp Keck
 */
public class OutOfBeans extends EventInstance {

    @InstanceParameter(ui = UIHint.IntegralSlider.class, min = 0, max = CoffeeMachine.BEAN_TANK_SIZE)
    private final int remainingBeans;

    /**
     * Creates a new OutOfBeans event instance.
     * 
     * @param event
     *            The event.
     * @param remainingBeans
     *            The number of remaining beans.
     */
    public OutOfBeans(Event<? extends EventInstance> event, int remainingBeans) {
        super(event);
        this.remainingBeans = remainingBeans;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public OutOfBeans(JsonNode node) {
        super(node);
        this.remainingBeans = node.get("remainingBeans").asInt();
    }

    /**
     * Gets the remaining beans.
     * 
     * @return The number of remaining beans.
     */
    public int getRemainingBeans() {
        return remainingBeans;
    }

}
