package de.uni_stuttgart.riot.rest;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;

/**
 * The thing service will handle any access (create, read, update, delete) to the "things".
 *
 */
@Path("thing")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ThingService extends BaseResource<RemoteThing> {

    private ThingLogic logic = new ThingLogic();

    /**
     * Default Constructor.
     * 
     * @throws SQLException .
     * @throws NamingException .
     */
    public ThingService() throws SQLException, NamingException {
        super(new RemoteThingSqlQueryDAO(ConnectionMgr.openConnection(), false));
    }

    @Override
    public void init(RemoteThing storable) throws Exception {
        this.logic.completeRemoteThing(storable);
    }
}
