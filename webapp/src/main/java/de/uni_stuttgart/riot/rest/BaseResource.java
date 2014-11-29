package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    /** The page size. */
    // protected int pageSize = 20;

    // protected static String URI_PATH;

    /** The model manager for read/write operations. */
    protected ModelManager<E> modelManager;

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
     */
    @GET
    @Path("{id}")
    @Produces(PRODUCED_FORMAT)
    public E getById(@PathParam("id") int id) {
        E model = modelManager.getById(id);
        if (model == null) {
            throw new NotFoundException();
        }
        return model;
    }

    /**
     * Gets the collection fo resources.
     *
     * @return the collection
     */
    @GET
    @Produces(PRODUCED_FORMAT)
    public Collection<E> get() {
        // TODO: pagination
        return modelManager.get();
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param model
     *            the model
     * @return an HTTP created (201) response if successful
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    @POST
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Response create(E model) throws URISyntaxException {
        E created = modelManager.create(model); // should throw an exception if
                                                // not successful
        URI relative = getUriForModel(created);
        return Response.created(relative).entity(created).build();
    }

    // TODO: PUT for model
    // TODO: put for collection

    /**
     * Deletes the model with the given id.
     *
     * @param id
     *            the id
     * @return the response
     */
    @DELETE
    @Path("{id}")
    @Consumes(CONSUMED_FORMAT)
    public Response delete(@PathParam("id") int id) {
        modelManager.delete(id); // should throw an exception if not successful
        return Response.noContent().build();
    }

    /**
     * Gets the uri for a model.
     *
     * @param model
     *            the model
     * @return the uri for model
     */
    protected abstract URI getUriForModel(E model);

}
