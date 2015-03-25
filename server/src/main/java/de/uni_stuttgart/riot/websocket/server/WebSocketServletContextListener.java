package de.uni_stuttgart.riot.websocket.server;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.websocket.WebSocketMessage;

/**
 * The web socket application that listens on <tt>/connect/{token}</tt>. See {@link WebSocketEndpoint} for the actual magic.
 * 
 * @author Philipp Keck
 */
@WebListener
public class WebSocketServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServletContextListener.class);

    /**
     * The key of the Tyrus web socket container within the servlet context.
     */
    private static final String WEBSOCKET_CONTAINER_KEY = "javax.websocket.server.ServerContainer";

    @Override
    public void contextInitialized(ServletContextEvent context) {
        ServletContext servletContext = context.getServletContext();
        ServerContainer websocketsContainer = (ServerContainer) servletContext.getAttribute(WEBSOCKET_CONTAINER_KEY);
        if (websocketsContainer == null) {
            LOGGER.error("Error when registering the web socket listener: The ServerContainer is not available. Web sockets will not work");
            return;
        }

        try {
            websocketsContainer.addEndpoint(ServerEndpointConfig.Builder //
                    .create(WebSocketEndpoint.class, WebSocketEndpoint.PATH) //
                    .configurator(new ShiroLoginConfigurator(servletContext)) //
                    .decoders(Arrays.asList(WebSocketMessage.Coder.class)) //
                    .encoders(Arrays.asList(WebSocketMessage.Coder.class)) //
                    .build());
        } catch (DeploymentException e) {
            LOGGER.error("Error when registering the web socket listener, web sockets will be unavailable", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) {
    }

}
