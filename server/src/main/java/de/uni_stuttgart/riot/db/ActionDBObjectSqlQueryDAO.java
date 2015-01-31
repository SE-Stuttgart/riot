package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.ActionDBObject;

/**
 * DAO for {@link ActionDBObject}.
 *
 */
public class ActionDBObjectSqlQueryDAO extends SqlQueryDAO<ActionDBObject> {

    @Override
    protected Class<ActionDBObject> getMyClazz() {
        return ActionDBObject.class;
    }

}
