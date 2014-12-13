package de.uni_stuttgart.riot.usermanagement.logic.test.common;

import java.io.File;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import de.uni_stuttgart.riot.usermanagement.test.common.SqlRunner;

public class LogicTestBase {

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private static DataSource dataSource;

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

        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        try {
            ic.destroySubcontext("jdbc");
        } catch (NameNotFoundException e) {
            // Ignore
        }

        ic.createSubcontext("jdbc");

        // Construct DataSource
        File tempFile = tempFolder.newFile();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:" + tempFile.getAbsolutePath());
        ds.setUser("sa");
        ds.setUser("sa");
        dataSource = ds;

        ic.rebind("jdbc/iot", ds);

    }

    @Before
    public void setUp() throws Exception {
        SqlRunner.runStartupScripts(dataSource, true);
    }
}
