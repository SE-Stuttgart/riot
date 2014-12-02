package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.Token;

public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    public TokenSqlQueryDAO(DataSource ds) {
        super(ds, new TokenQueryBuilder(), new TokenObjectBuilder());
    }

}
