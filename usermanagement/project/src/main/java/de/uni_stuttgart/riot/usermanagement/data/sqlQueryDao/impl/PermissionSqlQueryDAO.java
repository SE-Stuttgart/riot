package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;

/**
 * DAO for {@link Permission}
 * @author Jonas Tangermann
 *
 */
public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    /**
     * Constructor
     * @param ds The datasource to be used by the dao
     */
    public PermissionSqlQueryDAO(DataSource ds) {
        super(ds, new PermissionQueryBuilder(), new PermissionObjectBuilder());
    }

}
