package de.uni_stuttgart.riot.commons.test;

import java.net.URI;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.slf4j.bridge.SLF4JBridgeHandler;

import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;

/**
 * Base class for tests that need database access and Jersey. Note that Shiro will be completely disabled. To test a service with Shiro
 * activated, use <tt>ShiroEnabledTest</tt> from the user management project.
 */
public class JerseyDBTestBase extends JerseyTest {

    /**
     * This rule will manage the database.
     */
    @ClassRule
    public static H2DatabaseRule database = new H2DatabaseRule();

    /**
     * Detects {@link TestData} annotations and executes them.
     */
    @Rule
    public TestWatcher annotationWatcher = new TestDataWatcher(database);

    /**
     * Gets the JDBC data source.
     * 
     * @return The JDBC data source for this test instance.
     */
    protected static DataSource getDataSource() {
        return database.getDataSource();
    }

    @BeforeClass
    public static void redirectLogging() {
        // Jersey uses java.util.logging internally, we redirect it to slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("api/v1/").build();
    }

    @Override
    protected Application configure() {
        return new InsecureRiotApplication();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(RiotApplication.produceJacksonProvider());
    }

    /**
     * A special kind of the {@link RiotApplication} that only contains the actual REST services (not the security providers) so that we can
     * test our classes without security restrictions.
     * 
     * @author Philipp Keck
     */
    private static class InsecureRiotApplication extends RiotApplication {
        @Override
        protected void registerProviders() {
            packages(REST_PROVIDERS);
            // Note that we do not register the security providers here!
        }
    }

}
