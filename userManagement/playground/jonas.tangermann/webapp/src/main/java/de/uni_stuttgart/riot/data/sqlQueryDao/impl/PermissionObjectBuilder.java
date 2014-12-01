package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.sqlquerydao.ObjectBuilder;
import de.uni_stuttgart.riot.data.storable.Permission;

public class PermissionObjectBuilder implements ObjectBuilder<Permission> {

    @Override
    public Permission build(ResultSet resultSet) throws SQLException {
        return new Permission(resultSet.getLong(1), resultSet.getString(2));
    }

}
