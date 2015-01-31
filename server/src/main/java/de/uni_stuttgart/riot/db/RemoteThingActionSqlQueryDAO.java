package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

/**
 * DAO for {@link RemoteThingAction}.
 *
 */
public class RemoteThingActionSqlQueryDAO extends SqlQueryDAO<RemoteThingAction> {

    @Override
    protected Class<RemoteThingAction> getMyClazz() {
        return RemoteThingAction.class;
    }

}
