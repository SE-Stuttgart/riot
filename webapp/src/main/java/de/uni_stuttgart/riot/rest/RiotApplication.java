package de.uni_stuttgart.riot.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

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
        packages("de.uni_stuttgart.riot.rest;de.uni_stuttgart.riot.calendar;");
    }

}