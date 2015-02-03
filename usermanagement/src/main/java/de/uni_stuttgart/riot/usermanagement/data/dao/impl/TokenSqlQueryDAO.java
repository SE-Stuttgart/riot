package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * Data access class for all {@link Token} objects.
 * 
 * @author Jonas Tangermann
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    @Override
    protected Class<Token> getMyClazz() {
        return Token.class;
    }

}
