package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

/**
 * DAO for {@link UMUser}
 * @author Jonas Tangermann
 *
 */
public class UserSqlQueryDao extends SqlQueryDAO<UMUser> {

    /**
     * Constructor
     * @param ds The datasource to be used by the dao
     */
    public UserSqlQueryDao(DataSource ds) {
        super(ds, new UserQueryBuilder(), new UserObjectBuilder());
    }

    public UserSqlQueryDao(Connection connection) throws SQLException {
        super(connection, new UserQueryBuilder(), new UserObjectBuilder());
    }

}
