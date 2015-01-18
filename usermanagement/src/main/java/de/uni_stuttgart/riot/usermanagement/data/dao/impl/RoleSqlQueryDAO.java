package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * Data access class for all {@link Role} objects.
 * 
 * @author Jonas Tangermann
 *
 */
public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {

    public RoleSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<Role> getMyClazz() {
        return Role.class;
    }
}
