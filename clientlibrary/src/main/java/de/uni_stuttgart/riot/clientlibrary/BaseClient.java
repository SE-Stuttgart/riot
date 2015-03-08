package de.uni_stuttgart.riot.clientlibrary;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for "clients", that is for classes that encapsulate calls to the server into simply callable methods. This base class provides
 * common methods and authentication mechanisms. A "client" uses a server connector to connect the server.
 * 
 * @author Philipp Keck
 */
public abstract class BaseClient {

    private final ServerConnector serverConnector;

    /**
     * Creates a new BaseClient.
     * 
     * @param serverConnector
     *            The connector to be used to connect to the server.
     */
    protected BaseClient(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    /**
     * Gets the connector.
     * 
     * @return The connector used to execute requests.
     */
    protected ServerConnector getConnector() {
        return serverConnector;
    }

    /**
     * Gets the Jackson ObjectMapper.
     * 
     * @return The ObjectMapper for generating or parsing JSON.
     */
    public ObjectMapper getJsonMapper() {
        return serverConnector.getJsonMapper();
    }

}
