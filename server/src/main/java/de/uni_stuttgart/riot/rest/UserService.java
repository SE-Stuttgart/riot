package de.uni_stuttgart.riot.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.server.ThingLogic;

/**
 * Note that the main implementation is in {@link de.uni_stuttgart.riot.usermanagement.service.rest.UserService}. This class augments the
 * service by REST endpoints that are accessed through the <tt>/users</tt> path but actually refer to stuff that is not part of the
 * usermanagement project.
 * 
 * @author Philipp Keck
 */
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class UserService extends de.uni_stuttgart.riot.usermanagement.service.rest.UserService {

    /**
     * Creates a new instance.
     * 
     * @throws SQLException
     *             When initializing the underlying DAO fails.
     * @throws NamingException
     *             When the database connection is not registered in JNDI.
     */
    public UserService() throws SQLException, NamingException {
        super();
    }

    /**
     * Gets all things that are <em>owned</em> by a user. In order to get the things that a certain user may access (by sharing), use the
     * {@link ThingService}.
     *
     * @param userID
     *            The user ID.
     * @return The list of things that the given user is the owner of.
     */
    @GET
    @Path("/{userID}/things")
    public Collection<ThingInformation> getUserThings(@PathParam("userID") Long userID) {
        if (userID == null || userID < 1) {
            throw new BadRequestException("Invalid user ID " + userID);
        }
        return ThingLogic.getThingLogic().getAllThings(userID, ThingPermission.READ).map(ThingInformation::metaInfoOnly).collect(Collectors.toList());
    }

}
