package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * DAO for {@link Permission}.
 */
public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    protected Class<Permission> getMyClazz() {
        return Permission.class;
    }

}
