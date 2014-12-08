package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;

public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {

    public RoleSqlQueryDAO(DataSource ds) {
        super(ds, new RoleQueryBuilder(), new RoleObjectBuilder());
    }

}
