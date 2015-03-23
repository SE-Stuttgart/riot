package de.uni_stuttgart.riot.thing.factory.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An event instance to signal that the tank containing the processed pieces is full.
 */
public class FullProcessedPiecesTank extends EventInstance {

    @Parameter(ui = UIHint.IntegralSlider.class, min = 0, max = Machine.PROCESSED_PIECES_TANK_SIZE)
    private final int processedPieces;

    /**
     * Creates a new FullProcessedPiecesTank event instance.
     *
     * @param event
     *            The event.
     * @param processedPieces
     *            the number of processed pieces
     */
    public FullProcessedPiecesTank(Event<? extends EventInstance> event, int processedPieces) {
        super(event);
        this.processedPieces = processedPieces;
    }

    /**
     * Creates a new instance from JSON.
     * 
     * @param node
     *            The JSON node to read from.
     */
    @JsonCreator
    public FullProcessedPiecesTank(JsonNode node) {
        super(node);
        this.processedPieces = node.get("processedPieces").asInt();
    }

    /**
     * Gets the number of processed pieces.
     * 
     * @return The number of processed pieces.
     */
    public int getProcessedPieces() {
        return processedPieces;
    }
}
