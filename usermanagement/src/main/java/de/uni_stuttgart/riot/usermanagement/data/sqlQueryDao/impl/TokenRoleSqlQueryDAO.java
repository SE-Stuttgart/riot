package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;

/**
 * Data access class for all {@link Token} objects.
 * @author Jonas Tangermann
 *
 */
public class TokenRoleSqlQueryDAO extends SqlQueryDAO<TokenRole> {


    public TokenRoleSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection,transaction);
    }

    @Override
	protected Class<TokenRole> getMyClazz() {
		return TokenRole.class;
	}
}
