package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.EventDBObject;

public class EventDBObjectSqlQueryDAO extends SqlQueryDAO<EventDBObject>{

    @Override
    protected Class<EventDBObject> getMyClazz() {
        return EventDBObject.class;
    }

}
