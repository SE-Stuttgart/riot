package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

/**
 * DAO for {@link UMUser}.
 * 
 * @author Jonas Tangermann
 */
public class UserSqlQueryDAO extends SqlQueryDAO<UMUser> {

    @Override
    protected Class<UMUser> getMyClazz() {
        return UMUser.class;
    }

}
