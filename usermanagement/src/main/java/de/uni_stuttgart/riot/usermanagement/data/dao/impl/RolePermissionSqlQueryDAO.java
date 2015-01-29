package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;

/**
 * Data access class for all {@link RolePermission} objects.
 * 
 * @author Jonas Tangermann
 */
public class RolePermissionSqlQueryDAO extends SqlQueryDAO<RolePermission> {

    @Override
    protected Class<RolePermission> getMyClazz() {
        return RolePermission.class;
    }

}
