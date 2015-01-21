package de.uni_stuttgart.riot.server.commons.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

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
     * Configures the application.
     */
    public RiotApplication() {
        packages("de.uni_stuttgart.riot.rest", //
                "de.uni_stuttgart.riot.calendar", //
                "de.uni_stuttgart.riot.contacts", //
                "de.uni_stuttgart.riot.usermanagement.service.rest", //
                "de.uni_stuttgart.riot.usermanagement.security");
    }

}
