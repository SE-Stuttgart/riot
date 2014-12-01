package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.sqlquerydao.ObjectBuilder;
import de.uni_stuttgart.riot.data.storable.Role;

public class RoleObjectBuilder implements ObjectBuilder<Role> {

    @Override
    public Role build(ResultSet resultSet) throws SQLException {
        return new Role(resultSet.getLong(1), resultSet.getString(2));
    }

}
