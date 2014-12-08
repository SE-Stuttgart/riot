package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlquerydao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.TokenRole;

public class TokenRoleSqlQueryDAO extends SqlQueryDAO<TokenRole> {

    public TokenRoleSqlQueryDAO(DataSource ds) {
        super(ds, new TokenRoleQueryBuilder(), new TokenRoleObjectBuilder());
    }

}
