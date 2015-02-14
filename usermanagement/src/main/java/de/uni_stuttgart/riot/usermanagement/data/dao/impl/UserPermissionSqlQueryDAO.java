package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserPermission;

/**
 * Data access class for all {@link UserPermission} objects.
 * 
 * @author Jonas Tangermann
 */
public class UserPermissionSqlQueryDAO extends SqlQueryDAO<UserPermission> {

    @Override
    protected Class<UserPermission> getMyClazz() {
        return UserPermission.class;
    }

}
