package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;
import de.uni_stuttgart.riot.thing.remote.RemoteThingEvent;

/**
 * DAO for {@link RemoteThingAction}.
 *
 */
public class RemoteThingEventSqlQueryDAO extends SqlQueryDAO<RemoteThingEvent> {

    @Override
    protected Class<RemoteThingEvent> getMyClazz() {
        return RemoteThingEvent.class;
    }


}
