package de.uni_stuttgart.riot.usermanagement.application;

import javax.ws.rs.ApplicationPath;

import jersey.repackaged.com.google.common.collect.Sets;

import org.glassfish.jersey.server.ResourceConfig;

import de.uni_stuttgart.riot.usermanagement.security.AuthorizationExceptionMapper;
import de.uni_stuttgart.riot.usermanagement.security.AuthorizationFilterBinding;

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
public class UserManagementApplication extends ResourceConfig {

    /**
     * Configures the application.
     */
    public UserManagementApplication() {
        super(Sets.<Class<?>> newHashSet(AuthorizationFilterBinding.class, AuthorizationExceptionMapper.class));
        packages("de.uni_stuttgart.riot.usermanagement;");
    }
}
