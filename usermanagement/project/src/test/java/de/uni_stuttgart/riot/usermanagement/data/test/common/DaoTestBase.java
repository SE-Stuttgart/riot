package de.uni_stuttgart.riot.usermanagement.data.test.common;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

import de.uni_stuttgart.riot.usermanagement.test.common.SqlRunner;

public class DaoTestBase {

    protected DataSource ds;

    @Before
    public void setup() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("umdb");
        ds.setUser("umuser");
        ds.setPassword("1q2w3e4r");
        ds.setPortNumber(5432);
        ds.setServerName("localhost");
        SqlRunner.runStartupScripts(ds);
        this.ds = ds;
    }

    @After
    public void tearDown() {
    }
}
