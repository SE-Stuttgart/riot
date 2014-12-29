package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.net.URISyntaxException;
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

import de.uni_stuttgart.riot.db.DaoException;

/**
 * Base class for all rest resource classes.
 * 
 * Subclasses must specify a @Path annotation.
 *
 * @param <E>
 *            the element type
 */
public abstract class BaseResource<E extends ResourceModel> {

    /** Default format for serialization. */
    protected static final String PRODUCED_FORMAT = MediaType.APPLICATION_JSON;

    /** Default format for consumption. */
    protected static final String CONSUMED_FORMAT = MediaType.APPLICATION_JSON;

    /** The maximum page size. */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    // protected static String URI_PATH;

    /** The model manager for read/write operations. */
    protected ModelManager<E> modelManager;

    @Context
    private UriInfo uriInfo;

    /**
     * Instantiates a new base resource.
     *
     * @param modelManager
     *            the model manager
     */
    public BaseResource(ModelManager<E> modelManager) {
        this.modelManager = modelManager;
    }

    /**
     * Gets the resource model by id.
     *
     * @param id
     *            the id
     * @return the model if it exists, HTTP 404 otherwise
     * @throws DaoException
     *             when access not possible
     */
    @GET
    @Path("{id}")
    @Produces(PRODUCED_FORMAT)
    public E getById(@PathParam("id") long id) throws DaoException {
        E model = modelManager.getById(id);
        if (model == null) {
            throw new NotFoundException();
        }
        return model;
    }

    /**
     * Gets the collection for resources.
     *
     * @param offset
     *            the beginning item number
     * @param limit
     *            maximum number of items to return
     * @return the collection. If the both parameters are 0, it returns at maximum 20 elements.
     * @throws DaoException
     *             when retrieving the data fails
     */
    @GET
    @Produces(PRODUCED_FORMAT)
    public Collection<E> get(@QueryParam("offset") int offset, @QueryParam("limit") int limit) throws DaoException {

        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        } else if (limit == 0) {
            // the case when GET request has no query parameters (api/resource)
            return modelManager.get(offset, DEFAULT_PAGE_SIZE);

        } else {
            // the case when GET request has only limit query parameter (api/resource?limit=20)
            return modelManager.get(offset, limit);
        }
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param model
     *            the model
     * @return an HTTP created (201) response if successful
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws DaoException
     *             when creation not possible
     */
    @POST
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Response create(E model) throws URISyntaxException, DaoException {
        if (model == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        E created = modelManager.create(model);
        URI relative = getUriForModel(created);
        return Response.created(relative).entity(created).build();
    }

    /**
     * Updates the model with the given id.
     *
     * @param id
     *            the id
     * @param model
     *            the model
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     * @throws DaoException
     *             when update not possible
     * @throws URISyntaxException
     *             the URI syntax exception
     * 
     */
    @PUT
    @Path("{id}")
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Response update(@PathParam("id") long id, E model) throws DaoException {
        if (model == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        model.setId(id);
        if (modelManager.update(model)) {
            return Response.noContent().build();
        }
        throw new NotFoundException("No such resource exists.");
    }

    // TODO: put for collection

    /**
     * Deletes the model with the given id.
     *
     * @param id
     *            the id
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     * @throws DaoException
     *             when deletion not possible
     */
    @DELETE
    @Path("{id}")
    @Consumes(CONSUMED_FORMAT)
    public Response delete(@PathParam("id") long id) throws DaoException {
        if (modelManager.delete(id)) {
            return Response.noContent().build();
        }
        throw new NotFoundException("No such resource exists or it has already been deleted.");
    }

    /**
     * Gets the uri for a model.
     *
     * @param model
     *            the model
     * @return the uri for model
     */
    protected URI getUriForModel(E model) {
        return uriInfo.getBaseUriBuilder().path(this.getClass()).path(Long.toString(model.getId())).build();
    }

}
