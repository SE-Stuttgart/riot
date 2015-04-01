package de.uni_stuttgart.riot.websocket;

import de.uni_stuttgart.riot.thing.EventInstance;

/**
 * This web socket message wraps the relevant fields of an {@link EventInstance} after it was fired.
 * 
 * @author Niklas Schnabel
 */
public class PropertyChangeMessage extends WebSocketMessage {

    private final EventInstance eventInstance;

    /**
     * Creates a new instance.
     *
     * @param eventInstance
     *            The instance of the event
     */
    public PropertyChangeMessage(EventInstance eventInstance) {
        this.eventInstance = eventInstance;
    }

    public EventInstance getEventInstance() {
        return eventInstance;
    }
}
