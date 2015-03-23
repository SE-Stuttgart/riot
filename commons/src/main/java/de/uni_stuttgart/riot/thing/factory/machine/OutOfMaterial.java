package de.uni_stuttgart.riot.thing.factory.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An event instance to signal that there are only little or no material left.
 */
public class OutOfMaterial extends EventInstance {

    @Parameter(ui = UIHint.IntegralSlider.class, min = 0, max = Machine.MATERIAL_TANK_SIZE)
    private final int remainingPieces;

    /**
     * Creates a new OutOfMaterial event instance.
     * 
     * @param event
     *            The event.
     * @param remainingPieces
     *            The number of remaining pieces.
     */
    public OutOfMaterial(Event<? extends EventInstance> event, int remainingPieces) {
        super(event);
        this.remainingPieces = remainingPieces;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public OutOfMaterial(JsonNode node) {
        super(node);
        this.remainingPieces = node.get("remainingPieces").asInt();
    }

    /**
     * Gets the remaining pieces.
     * 
     * @return The number of remaining material pieces.
     */
    public int getRemainingPieces() {
        return remainingPieces;
    }
}
