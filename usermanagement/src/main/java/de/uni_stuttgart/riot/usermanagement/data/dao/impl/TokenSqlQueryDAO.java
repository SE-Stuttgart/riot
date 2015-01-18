package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * Data access class for all {@link Token} objects
 * 
 * @author Jonas Tangermann
 *
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    public TokenSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<Token> getMyClazz() {
        return Token.class;
    }

}
