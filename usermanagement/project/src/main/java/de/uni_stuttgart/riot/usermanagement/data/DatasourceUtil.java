package de.uni_stuttgart.riot.usermanagement.data;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatasourceUtil {

    private static final String JNDI_PATH = "jdbc/iot";

    public static DataSource getDataSource() throws NamingException {
        InitialContext in;
        in = new InitialContext();
        return (DataSource) in.lookup(JNDI_PATH); // FIXME correct jndi path
    }

    public static Long nextId() {
        return 42L;
    }

}
