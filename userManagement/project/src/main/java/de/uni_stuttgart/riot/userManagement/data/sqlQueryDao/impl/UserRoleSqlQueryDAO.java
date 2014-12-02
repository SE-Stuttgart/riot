package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.UserRole;

public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole> {

    public UserRoleSqlQueryDAO(DataSource ds) {
        super(ds, new UserRoleQueryBuilder(), new UserRoleObjectBuilder());
    }

}
