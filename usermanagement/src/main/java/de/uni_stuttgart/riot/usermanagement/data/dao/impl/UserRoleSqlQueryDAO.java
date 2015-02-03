package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

/**
 * DAO for {@link UserRole}.
 * 
 * @author Jonas Tangermann
 */
public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole> {

    @Override
    protected Class<UserRole> getMyClazz() {
        return UserRole.class;
    }

}
