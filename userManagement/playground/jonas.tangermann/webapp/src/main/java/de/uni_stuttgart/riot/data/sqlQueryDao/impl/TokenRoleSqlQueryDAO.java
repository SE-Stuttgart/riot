package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.TokenRole;

public class TokenRoleSqlQueryDAO extends SqlQueryDAO<TokenRole>{

	public TokenRoleSqlQueryDAO(DataSource ds) {
		super(ds, new TokenRoleQueryBuilder(), new TokenRoleObjectBuilder());
	}

}