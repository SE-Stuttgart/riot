package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;

/**
 * {@link ObjectBuilder} for {@link Role}. Used in {@link RoleSqlQueryDAO}.
 * @author Jonas Tangermann
 *
 */
public class RoleObjectBuilder implements ObjectBuilder<Role> {

    @Override
    public Role build(ResultSet resultSet) throws SQLException {
        return new Role(resultSet.getLong(1), resultSet.getString(2));
    }

}
