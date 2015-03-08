package de.uni_stuttgart.riot.clientlibrary;

import java.io.IOException;

import org.junit.Before;

import de.uni_stuttgart.riot.commons.test.ShiroEnabledTest;

/**
 * Base class for tests that test a "client" and therefore need a ServerConnector that is logged in.
 * 
 * @author Philipp Keck
 */
public abstract class BaseClientTest extends ShiroEnabledTest {

    private ServerConnector connector;

    @Before
    public void initializeConnector() throws IOException, RequestException {
        connector = produceNewServerConnector();
        connector.login("Yoda", "YodaPW");
    }

    /**
     * Gets the connector.
     * 
     * @return A connector that can be used for new clients to be tested.
     */
    protected ServerConnector getLoggedInConnector() {
        return connector;
    }

    /**
     * Gets a new connector.
     * 
     * @return A new server connector that is not yet logged in but points to the unit test server and contains a token manager that simply
     *         stores the tokens.
     */
    protected ServerConnector produceNewServerConnector() {
        return new ServerConnector(new ConnectionInformation("http", "localhost", getPort(), "/api/v1/"), new DefaultTokenManager());
    }

}
