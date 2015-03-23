package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleDescriptions;
import de.uni_stuttgart.riot.rule.server.RuleLogic;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The rule service will handle any access (create, read, update, delete) to the {@link Rule}s.
 */
@Path("rules")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class RuleService {

    /** The maximum page size. */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    private final RuleLogic logic = RuleLogic.getRuleLogic();

    @Context
    private UriInfo uriInfo;

    /**
     * Gets the current configuration of a rule. This is a JSON object containing the rule's type, name, id, parameters and status.
     *
     * @param id
     *            The id of the rule.
     * @return The rule's configuration if it exists, HTTP 404 otherwise
     */
    @GET
    @Path("{id}")
    public RuleConfiguration getRuleConfiguration(@PathParam("id") long id) {
        RuleConfiguration config = logic.getRuleConfiguration(id);
        if (config == null) {
            throw new NotFoundException();
        } else {
            return config;
        }
    }

    /**
     * Gets a description of a rule type. See {@link RuleDescription} for details about the JSON format.
     *
     * @param type
     *            The fully qualified class name to identify the rule type.
     * @return A description of the rule's structure (or 404 if the rule type does not exist).
     */
    @GET
    @Path("description/{type}")
    public RuleDescription getRuleDescription(@PathParam("type") String type) {
        try {
            return RuleDescriptions.get(type);
        } catch (ClassCastException | ClassNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Gets a list of all known rule descriptions. This can be used to display to the user and then let him/her choose which rule to
     * instantiate.
     * 
     * @return All rule descriptions.
     */
    @GET
    @Path("descriptions")
    public Collection<RuleDescription> getRuleDescriptions() {
        return RuleDescriptions.getAll(true);
    }

    /**
     * Gets the collection for resources.
     *
     * @param offset
     *            the beginning item number
     * @param limit
     *            maximum number of items to return
     * @return the collection. If the both parameters are 0, it returns at maximum 20 elements.
     */
    @GET
    public Collection<RuleConfiguration> get(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }

        // Fetch the results
        if (limit == 0) {
            return logic.findRules(offset, DEFAULT_PAGE_SIZE, null);
        } else {
            return logic.findRules(offset, limit, null);
        }
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param request
     *            object specifying the filter attributes (pagination also possible)
     * @return collection containing elements that applied to filter
     * @throws DatasourceFindException
     *             when retrieving the data fails
     */
    //FIXME: should use query attribute filtering like the base resource
    @POST
    @Path("/filter")
    public Collection<RuleConfiguration> getBy(FilteredRequest request) throws DatasourceFindException {
        if (request == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        return logic.findRules(request, null);
    }

    /**
     * Creates a new rule and starts it.
     *
     * @param config
     *            The request data for creating the rule. This is just a rule configuration.
     * @return An HTTP created (201) response if successful
     * @throws DatasourceInsertException
     *             When insertion failed
     */
    @POST
    public Response addNewRule(RuleConfiguration config) throws DatasourceInsertException {
        if (config == null) {
            throw new BadRequestException("Please provide an entity in the request body.");
        }

        // Find out the owner (which is the current user).
        UserManagementFacade umFacade = UserManagementFacade.getInstance();
        long ownerId = umFacade.getCurrentUserId();

        // Create the rule.
        RuleConfiguration newConfig = this.logic.addNewRule(config, ownerId);
        return Response.created(getUriForRule(newConfig)).entity(newConfig).build();
    }

    /**
     * Updates the configuration of a rule. This can be used to start and stop the rule.
     * 
     * @param config
     *            The new configuration for the rule.
     * @return HTTP 200 if successful.
     * @throws DatasourceUpdateException
     *             When updating the configuration failed.
     */
    @PUT
    @Path("{id}")
    public Response updateRuleConfiguration(RuleConfiguration config) throws DatasourceUpdateException {
        if (config == null) {
            throw new BadRequestException("Please provide an entity in the request body.");
        }

        // Find out the owner (which is the current user).
        UserManagementFacade umFacade = UserManagementFacade.getInstance();
        long ownerId = umFacade.getCurrentUserId();
        if (config.getOwnerId() != ownerId) {
            throw new UnauthorizedException("Can't change config " + config.getId());
        }

        logic.updateRuleConfiguration(config);
        return Response.noContent().build();
    }

    /**
     * Deletes the rule with the given id.
     *
     * @param id
     *            the id
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     */
    @DELETE
    @Path("{id}")
    public Response deleteRule(@PathParam("id") long id) {
        try {
            this.logic.deleteRule(id);
        } catch (DatasourceDeleteException exception) {
            throw new NotFoundException("No such resource exists or it has already been deleted.", exception);
        }
        return Response.noContent().build();
    }

    /**
     * Gets the uri for a rule.
     *
     * @param config
     *            the rule configuration
     * @return the uri for the rule
     */
    protected URI getUriForRule(RuleConfiguration config) {
        return uriInfo.getBaseUriBuilder().path(this.getClass()).path(Long.toString(config.getId())).build();
    }

}
