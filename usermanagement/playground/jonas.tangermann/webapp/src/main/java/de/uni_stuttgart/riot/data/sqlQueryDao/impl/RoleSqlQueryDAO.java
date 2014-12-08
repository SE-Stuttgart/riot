package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlquerydao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Role;

public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {

    public RoleSqlQueryDAO(DataSource ds) {
        super(ds, new RoleQueryBuilder(), new RoleObjectBuilder());
    }

}
