package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;

/**
 * Data access class for all {@link Token} objects.
 * @author Jonas Tangermann
 *
 */
public class TokenRoleSqlQueryDAO extends SqlQueryDAO<TokenRole> {

    public TokenRoleSqlQueryDAO(DataSource ds) {
        super(ds, new TokenRoleQueryBuilder(), new TokenRoleObjectBuilder());
    }

}
