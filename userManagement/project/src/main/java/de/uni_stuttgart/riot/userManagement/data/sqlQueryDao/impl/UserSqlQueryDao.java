package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.User;

public class UserSqlQueryDao extends SqlQueryDAO<User> {

    public UserSqlQueryDao(DataSource ds) {
        super(ds, new UserQueryBuilder(), new UserObjectBuilder());
    }

}
