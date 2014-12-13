package de.uni_stuttgart.riot.usermanagement.security;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

/**
 * The AuthenticationFilterBinding will register the authentication annotation of shiro to be usable as part of the JAX-RS implementation of
 * jersey.
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
public class AuthenticationFilterBinding implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().isAnnotationPresent(RequiresAuthentication.class)) {
            context.register(AuthenticationFilter.class);
        }
    }

}
