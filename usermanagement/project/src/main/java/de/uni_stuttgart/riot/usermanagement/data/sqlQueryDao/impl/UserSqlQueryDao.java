package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;

/**
 * DAO for {@link User}
 * @author Jonas Tangermann
 *
 */
public class UserSqlQueryDao extends SqlQueryDAO<User> {

    /**
     * Constructor
     * @param ds The datasource to be used by the dao
     */
    public UserSqlQueryDao(DataSource ds) {
        super(ds, new UserQueryBuilder(), new UserObjectBuilder());
    }

}
