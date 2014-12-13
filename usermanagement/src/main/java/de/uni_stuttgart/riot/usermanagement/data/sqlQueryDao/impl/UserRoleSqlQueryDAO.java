package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

/**
 * DAO for {@link UserRole}
 * @author Jonas Tangermann
 *
 */
public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole> {

    /**
     * Constructor
     * @param ds The datasource to be used by the dao
     */
    public UserRoleSqlQueryDAO(DataSource ds) {
        super(ds, new UserRoleQueryBuilder(), new UserRoleObjectBuilder());
    }

}
