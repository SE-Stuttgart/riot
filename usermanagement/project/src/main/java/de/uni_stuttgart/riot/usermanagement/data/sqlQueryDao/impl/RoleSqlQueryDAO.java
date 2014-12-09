package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;

/**
 * Data access class for all {@link Role} objects.
 * @author Jonas Tangermann
 *
 */
public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {

    /**
     * Constructor
     * @param ds {@link DataSource} that should be used.
     */
    public RoleSqlQueryDAO(DataSource ds) {
        super(ds, new RoleQueryBuilder(), new RoleObjectBuilder());
    }

}
