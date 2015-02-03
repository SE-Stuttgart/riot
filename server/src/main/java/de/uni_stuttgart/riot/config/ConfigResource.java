package de.uni_stuttgart.riot.config;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.server.commons.config.ConfigurationDAO;
import de.uni_stuttgart.riot.server.commons.config.ConfigurationStorable;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * The Class ConfigRessource.
 * 
 * @author Niklas Schnabel
 */
@Path("config")
public class ConfigResource extends BaseResource<ConfigurationStorable> {

    /**
     * Instantiates a new config ressource.
     */
    public ConfigResource() {
        super(new ConfigurationDAO());
    }

    @Override
    public void init(ConfigurationStorable storable) throws Exception {

    }

    /**
     * Gets the configuration by configuration key.
     *
     * @param key
     *            the key
     * @return the model if it exists, HTTP 404 otherwise
     * @throws DatasourceFindException
     *             when resource was not found
     */
    @GET
    @Path("key/{key}")
    @Produces(PRODUCED_FORMAT)
    public ConfigurationStorable getByKey(@PathParam("key") String key) throws DatasourceFindException {
        try {
            ConfigurationStorable result = dao.findByUniqueField(new SearchParameter(SearchFields.CONFIGKEY, key));
            return result;
        } catch (de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }

    }

    /**
     * Gets the configuration by configuration key.
     *
     * @param key
     *            the key
     * @return the model if it exists, HTTP 404 otherwise
     * @throws DatasourceFindException
     *             when resource was not found
     */
    @DELETE
    @Path("key/{key}")
    @Produces(PRODUCED_FORMAT)
    public Response deleteByKey(@PathParam("key") String key) throws DatasourceFindException {
        try {
            ConfigurationStorable result = dao.findByUniqueField(new SearchParameter(SearchFields.CONFIGKEY, key));
            dao.delete(result);
        } catch (de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
        return Response.noContent().build();
    }

}
