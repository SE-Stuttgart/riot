package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

/**
 * Data access class for all {@link Token} objects
 * @author Jonas Tangermann
 *
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    /**
     * Constructor
     * @param ds datasource that should be used.
     */
    public TokenSqlQueryDAO(DataSource ds) {
        super(ds, new TokenQueryBuilder(), new TokenObjectBuilder());
    }

    public TokenSqlQueryDAO(Connection connection) throws SQLException {
        super(connection, new TokenQueryBuilder(), new TokenObjectBuilder());
    }

}
