package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;

/**
 * {@link ObjectBuilder} for {@link RolePermission}. Used in {@link RolePermissionSqlQueryDAO}.
 * @author Jonas Tangermann
 *
 */
public class RolePermissionObjectBuilder implements ObjectBuilder<RolePermission> {

    @Override
    public RolePermission build(ResultSet resultSet) throws SQLException {
        return new RolePermission(resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3));
    }

}
