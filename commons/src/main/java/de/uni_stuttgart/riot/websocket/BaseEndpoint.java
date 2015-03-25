package de.uni_stuttgart.riot.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for websocket endpoints that contains common methods for sending/receiving messages.
 * 
 * @author Philipp Keck
 */
public abstract class BaseEndpoint extends Endpoint {

    /**
     * The logger for this endpoint.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        session.addMessageHandler(WebSocketMessage.class, new MessageHandler.Whole<WebSocketMessage>() {
            public void onMessage(WebSocketMessage message) {
                BaseEndpoint.this.onMessage(message, session);
            }
        });
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.error("Error in websocket connection, closing", thr);
        CloseReason closeReason = new CloseReason(CloseCodes.UNEXPECTED_CONDITION, "Error occured: " + thr.getClass().getName());
        try {
            session.close();
        } catch (IOException e) {
            // The closing failed, so probably the client is already gone. Just to be sure, we call the onclose method.
            onClose(session, closeReason);
        }
    }

    /**
     * This method is executed whenever a message is received.
     * 
     * @param message
     *            The received message.
     * @param session
     *            The web socket session.
     */
    public abstract void onMessage(WebSocketMessage message, Session session);

}
