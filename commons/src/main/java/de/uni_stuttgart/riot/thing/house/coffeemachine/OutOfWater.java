package de.uni_stuttgart.riot.thing.house.coffeemachine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An event instance to signal that there is only little or no water left in the tank.
 * 
 * @author Philipp Keck
 */
public class OutOfWater extends EventInstance {

    @Parameter(ui = UIHint.FractionalSlider.class, min = 0, max = CoffeeMachine.WATER_TANK_SIZE)
    private final double remainingWater;

    /**
     * Creates a new OutOfWater event instance.
     * 
     * @param event
     *            The event.
     * @param remainingWater
     *            The amount of remaining water in milliliters.
     */
    public OutOfWater(Event<? extends EventInstance> event, double remainingWater) {
        super(event);
        this.remainingWater = remainingWater;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public OutOfWater(JsonNode node) {
        super(node);
        this.remainingWater = node.get("remainingWater").asDouble();
    }

    /**
     * Gets the remaining water.
     * 
     * @return The amount of remaining water in milliliters.
     */
    public double getRemainingWater() {
        return remainingWater;
    }

}
