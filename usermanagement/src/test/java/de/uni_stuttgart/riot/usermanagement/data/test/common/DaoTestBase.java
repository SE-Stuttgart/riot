package de.uni_stuttgart.riot.usermanagement.data.test.common;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import de.uni_stuttgart.riot.usermanagement.test.common.SqlRunner;

public class DaoTestBase {
    
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();
    
    private DataSource dataSource;

    @Before
    public void setup() throws IOException {
        File tempFile = tempFolder.newFile();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:" + tempFile.getAbsolutePath());
        ds.setUser("sa");
        ds.setUser("sa");
        SqlRunner.runStartupScripts(ds);
        dataSource = ds;
    }

    @After
    public void tearDown() {
    }
    
    protected DataSource getDataSource() {
        return dataSource;
    }

}
