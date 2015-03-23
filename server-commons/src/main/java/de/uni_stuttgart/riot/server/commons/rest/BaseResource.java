package de.uni_stuttgart.riot.server.commons.rest;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * Base class for all rest resource classes.
 * 
 * Subclasses must specify a @Path annotation.
 *
 * @param <E>
 *            the element type
 */
public abstract class BaseResource<E extends Storable> {

    /** Default format for serialization. */
    protected static final String PRODUCED_FORMAT = MediaType.APPLICATION_JSON;

    /** Default format for consumption. */
    protected static final String CONSUMED_FORMAT = MediaType.APPLICATION_JSON;

    /** The maximum page size. */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    // protected static String URI_PATH;

    /** The DAO for read/write operations. */
    protected DAO<E> dao;

    @Context
    private UriInfo uriInfo;

    /**
     * Instantiates a new base resource.
     *
     * @param modelManager
     *            the model manager
     */
    public BaseResource(DAO<E> modelManager) {
        this.dao = modelManager;
    }

    /**
     * Gets the resource model by id.
     *
     * @param id
     *            the id
     * @return the model if it exists, HTTP 404 otherwise
     * @throws DatasourceFindException
     *             when resource was not found
     */
    @GET
    @Path("{id}")
    @Produces(PRODUCED_FORMAT)
    public E getById(@PathParam("id") long id) throws DatasourceFindException {
        try {
            E result = dao.findBy(id);
            this.init(result);
            return result;
        } catch (de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }

    }

    /**
     * Initializes given parameter object, for example by filling it with its children, if necessary.
     * 
     * @param storable
     *            object to be initialized / filled.
     * @throws Exception .
     */
    public abstract void init(E storable) throws Exception;

    /**
     * Creates a new model with data from the request body.
     *
     * @param info
     *            the info
     * @param offset
     *            the offset
     * @param limit
     *            the limit
     * @return collection containing elements that applied to filter
     * @throws DatasourceFindException
     *             when retrieving the data fails
     */
    @GET
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Collection<E> getBy(@Context UriInfo info, @QueryParam("offset") int offset, @DefaultValue("20") @QueryParam("limit") int limit) throws DatasourceFindException {
        FilteredRequest request = new FilteredRequest();
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }
        request.setLimit(limit);
        request.setOffset(offset);
        request.parseQueryParams(info.getQueryParameters().entrySet());
        try {
            Collection<E> result = dao.findAll(request);
            for (E e : result) {
                this.init(e);
            }
            return result;

        } catch (de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e1) {
            throw new DatasourceFindException(e1);
        }
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param model
     *            the model
     * @return an HTTP created (201) response if successful
     * @throws DatasourceInsertException
     *             when insertion failed
     */
    @POST
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Response create(E model) throws DatasourceInsertException {
        if (model == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        dao.insert(model);
        URI relative = getUriForModel(model);
        return Response.created(relative).entity(model).build();
    }

    /**
     * Updates the model with the given id.
     *
     * @param id
     *            the id
     * @param model
     *            the model
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     * 
     */
    @PUT
    @Path("{id}")
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Response update(@PathParam("id") long id, E model) {
        if (model == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        model.setId(id);

        try {
            dao.update(model);
        } catch (DatasourceUpdateException exception) {
            throw new NotFoundException("No such resource exists.", exception);
        }

        return Response.noContent().build();
    }

    // TODO: put for collection

    /**
     * Deletes the model with the given id.
     *
     * @param id
     *            the id
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     */
    @DELETE
    @Path("{id}")
    @Consumes(CONSUMED_FORMAT)
    public Response delete(@PathParam("id") long id) {
        try {
            dao.delete(id);
        } catch (DatasourceDeleteException exception) {
            throw new NotFoundException("No such resource exists or it has already been deleted.", exception);
        }

        return Response.noContent().build();
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
