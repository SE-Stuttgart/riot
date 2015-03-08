package de.uni_stuttgart.riot.server.commons.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * <p>
 * Configuration for the JAX-RS provider.
 * </p>
 * 
 * <p>
 * A list of all configured resources can be obtained with a GET request to <i>domain/applicationPath</i>/application.wadl
 * </p>
 * 
 * @see https://jersey.java.net/documentation/latest/deployment.html
 */
@ApplicationPath("/api/v1/*")
public class RiotApplication extends ResourceConfig {

    /**
     * List of packages that contain REST services.
     */
    protected static final String[] REST_SERVICES = { "de.uni_stuttgart.riot.rest", //
            "de.uni_stuttgart.riot.server.commons.rest", //
            "de.uni_stuttgart.riot.calendar", //
            "de.uni_stuttgart.riot.contacts", //
            "de.uni_stuttgart.riot.config", //
            "de.uni_stuttgart.riot.usermanagement.service.rest" };

    /**
     * List of packages that contain REST security providers.
     */
    protected static final String[] REST_SECUTRITY_PROVIDERS = { "de.uni_stuttgart.riot.usermanagement.security" };

    /**
     * List of packages that contain other REST providers.
     */
    protected static final String[] REST_PROVIDERS = { "de.uni_stuttgart.riot.server.commons.rest.provider" };

    /**
     * Configures the application.
     */
    public RiotApplication() {
        registerProviders();
        registerServices();
        registerJacksonProvider();
    }

    /**
     * Registers Jersey providers.
     */
    protected void registerProviders() {
        packages(REST_PROVIDERS);
        packages(REST_SECUTRITY_PROVIDERS);
    }

    /**
     * Registers all services.
     */
    protected void registerServices() {
        packages(REST_SERVICES);
    }

    /**
     * Registers the JSON provider.
     */
    protected void registerJacksonProvider() {
        register(JsonParseExceptionMapper.class);
        register(JsonMappingExceptionMapper.class);
        register(produceJacksonProvider());
    }

    /**
     * Produces a JSON provider with the correct configuration. This method is public for the use by other application parts that need the
     * same provider.
     * 
     * @return The Jackson provider.
     */
    public static JacksonJaxbJsonProvider produceJacksonProvider() {
        JacksonJaxbJsonProvider p = new JacksonJaxbJsonProvider();
        p.setMapper(produceObjectMapper());
        return p;
    }

    /**
     * Produces a Jackson ObjectMapper with the correct configuration. This method is public for the use by other application parts that
     * need the same mapper.
     * 
     * @return The Jackson ObjectMapper.
     */
    public static ObjectMapper produceObjectMapper() {
        return new ObjectMapper(); // Currently there is no configuration necessary.
    }

}
