package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;

/**
 * Data access class for all {@link Token} objects.
 * 
 * @author Jonas Tangermann
 */
public class TokenRoleSqlQueryDAO extends SqlQueryDAO<TokenRole> {

    @Override
    protected Class<TokenRole> getMyClazz() {
        return TokenRole.class;
    }

}
