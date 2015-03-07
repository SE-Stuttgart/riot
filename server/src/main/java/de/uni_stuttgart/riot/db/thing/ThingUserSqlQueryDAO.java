package de.uni_stuttgart.riot.db.thing;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * DAO for ThingUser entities.
 * 
 * @author Philipp Keck
 */
public class ThingUserSqlQueryDAO extends SqlQueryDAO<ThingUser> {

    @Override
    protected Class<ThingUser> getMyClazz() {
        return ThingUser.class;
    }

}
