package de.uni_stuttgart.riot.server.commons.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
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
    public static final String[] REST_SERVICES = { "de.uni_stuttgart.riot.rest", //
            "de.uni_stuttgart.riot.server.commons.rest", //
            "de.uni_stuttgart.riot.calendar", //
            "de.uni_stuttgart.riot.contacts", //
            "de.uni_stuttgart.riot.config", //
            "de.uni_stuttgart.riot.usermanagement.service.rest" };

    /**
     * List of packages that contain REST security providers.
     */
    public static final String[] REST_SECUTRITY_PROVIDERS = { "de.uni_stuttgart.riot.usermanagement.security" };

    /**
     * List of packages that contain other REST providers.
     */
    public static final String[] REST_PROVIDERS = { "de.uni_stuttgart.riot.server.commons.rest.provider" };

    /**
     * Configures the application.
     */
    public RiotApplication() {
        this(true);
    }

    /**
     * Configures the application.
     * 
     * @param jsonTyping
     *            if json serialization should add type information
     */
    public RiotApplication(boolean jsonTyping) {
        packages(REST_PROVIDERS);
        packages(REST_SECUTRITY_PROVIDERS);
        packages(REST_SERVICES);

        if (jsonTyping) {
            JacksonJaxbJsonProvider p = new JacksonJaxbJsonProvider();
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
            p.setMapper(mapper);
            register(p);
        }
    }

}
