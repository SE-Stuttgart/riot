package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    public TokenSqlQueryDAO(DataSource ds) {
        super(ds, new TokenQueryBuilder(), new TokenObjectBuilder());
    }

}
