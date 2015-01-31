package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.PropertyDBObject;

/**
 * DAO for {@link PropertyDBObject}.
 *
 */
public class PropertyDBObjectSqlQueryDAO extends SqlQueryDAO<PropertyDBObject> {

    @Override
    protected Class<PropertyDBObject> getMyClazz() {
        return PropertyDBObject.class;
    }
}
