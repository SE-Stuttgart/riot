package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * Data access class for all {@link Role} objects.
 * 
 * @author Jonas Tangermann
 */
public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {

    @Override
    protected Class<Role> getMyClazz() {
        return Role.class;
    }

}
