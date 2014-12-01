package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlquerydao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.UserRole;

public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole> {

    public UserRoleSqlQueryDAO(DataSource ds) {
        super(ds, new UserRoleQueryBuilder(), new UserRoleObjectBuilder());
    }

}
