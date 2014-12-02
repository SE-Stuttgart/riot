package de.uni_stuttgart.riot.userManagement.data.test.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

public class DaoTestBase {

    public DataSource ds;

    @Before
    public void setup() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("umdb");
        ds.setUser("niklas");
        ds.setPassword("wurscht");
        ds.setPortNumber(5432);
        ds.setServerName("localhost");
        this.ds = ds;
        this.runStartupScripts();
    }

    private void runStartupScripts() {
        try {
            SqlRunner runner = new SqlRunner(ds.getConnection(), new PrintWriter(System.out), new PrintWriter(System.err), true, false);
            runner.runScript(new FileReader(new File("src/main/resources/createschema.sql")));
            runner.runScript(new FileReader(new File("src/main/resources/insertTestValues.sql")));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }
}
