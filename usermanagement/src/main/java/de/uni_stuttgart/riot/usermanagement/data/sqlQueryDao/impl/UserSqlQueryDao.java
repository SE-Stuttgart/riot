package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;


/**
 * DAO for {@link UMUser}
 * @author Jonas Tangermann
 *
 */
public class UserSqlQueryDao extends SqlQueryDAO<UMUser> {


    public UserSqlQueryDao(Connection connection) throws SQLException {
        super(connection, new UserQueryBuilder(), new UserObjectBuilder());
    }

	@Override
	protected Class<UMUser> getMyClazz() {
		return UMUser.class;
	}

}
