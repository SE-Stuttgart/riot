package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

/**
 * Data access class for all {@link Token} objects
 * @author Jonas Tangermann
 *
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {


    public TokenSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection,transaction);
    }
    

	@Override
	protected Class<Token> getMyClazz() {
		return Token.class;
	}


}
