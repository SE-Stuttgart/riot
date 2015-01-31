package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;

/**
 * DAO for {@link RemoteThing}.
 *
 */
public class RemoteThingSqlQueryDAO extends SqlQueryDAO<RemoteThing> {

    @Override
    protected Class<RemoteThing> getMyClazz() {
        return RemoteThing.class;
    }

}
