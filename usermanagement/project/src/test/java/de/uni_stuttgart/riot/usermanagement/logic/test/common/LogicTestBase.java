package de.uni_stuttgart.riot.usermanagement.logic.test.common;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.postgresql.ds.PGSimpleDataSource;

import de.uni_stuttgart.riot.usermanagement.test.common.SqlRunner;

public class LogicTestBase {

    private static PGSimpleDataSource ds;

    @BeforeClass
    public static void setUpClass() throws Exception {
        /*
         * https://blogs.oracle.com/randystuph/entry/injecting_jndi_datasources_for_junit
         * 
         * Needed to add the following maven dependency:
         * 
         * <dependency> <!-- Needed for creating an intial context, so testing works without glassfish -->
         * <groupId>org.apache.tomcat</groupId> <artifactId>tomcat-catalina</artifactId> <version>8.0.15</version> <scope>test</scope>
         * </dependency>
         */
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("jdbc");

            // Construct DataSource
            ds = new PGSimpleDataSource();
            ds.setDatabaseName("umdb");
            ds.setUser("umuser");
            ds.setPassword("1q2w3e4r");
            ds.setPortNumber(5432);
            ds.setServerName("localhost");

            ic.bind("jdbc/iot", ds);
        } catch (NamingException ex) {
            ex.printStackTrace();
        }

    }

    @Before
    public void setUp() throws Exception {
        SqlRunner.runStartupScripts(ds, true);
    }
}
