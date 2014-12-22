package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;

/**
 * {@link ObjectBuilder} for {@link Permission}s. Used in {@link PermissionSqlQueryDAO}
 * @author Jonas Tangermann
 *
 */
public class PermissionObjectBuilder implements ObjectBuilder<Permission> {

    @Override
    public Permission build(ResultSet resultSet) throws SQLException {
        return new Permission(resultSet.getLong(1), resultSet.getString(2));
    }

}
