package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;

public class UserSqlQueryDao extends SqlQueryDAO<User> {

    public UserSqlQueryDao(DataSource ds) {
        super(ds, new UserQueryBuilder(), new UserObjectBuilder());
    }

}
