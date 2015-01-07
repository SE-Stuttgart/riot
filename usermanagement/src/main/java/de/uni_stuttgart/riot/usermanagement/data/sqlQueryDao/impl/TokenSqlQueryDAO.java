package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

/**
 * Data access class for all {@link Token} objects
 * @author Jonas Tangermann
 *
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {


    public TokenSqlQueryDAO(Connection connection) throws SQLException {
        super(connection);
    }
    

	@Override
	protected Class<Token> getMyClazz() {
		return Token.class;
	}


}
