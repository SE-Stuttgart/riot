package de.uni_stuttgart.riot.thing.car;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.InstanceParameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

public class OutOfGasoline extends EventInstance {

    @InstanceParameter(ui = UIHint.IntegralSlider.class, min = 0, max = Car.TANK_MAX_FILL_LEVEL)
    private final double remainingGasoline;

    /**
     * Creates a new OutOfGasoline event instance.
     * 
     * @param event
     *            The event.
     * @param remainingGasoline
     *            The amount of remaining gasoline in liter.
     */
    public OutOfGasoline(Event<? extends EventInstance> event, int remainingGasoline) {
        super(event);
        this.remainingGasoline = remainingGasoline;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public OutOfGasoline(JsonNode node) {
        super(node);
        this.remainingGasoline = node.get("remainingGasoline").asDouble();
    }

    /**
     * Gets the remaining amount of gasoline in liter.
     * 
     * @return The number of remaining beans.
     */
    public double getRemainingGasoline() {
        return remainingGasoline;
    }

}