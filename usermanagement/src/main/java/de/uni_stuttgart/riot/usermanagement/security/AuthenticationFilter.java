package de.uni_stuttgart.riot.usermanagement.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;

/**
 * The AuthenticationFilter will be used together with the {@link AuthenticationFilterBinding} class to enable the authentication annotation
 * of shiro to be usable as part of the JAX-RS implementation of jersey. The class will hook into the request call of a HTTP request,
 * perform a login based on the access token and grant or deny access.
 * 
 * @author Marcel Lehwald
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    protected static final String AUTHENTICATION_HEADER = "Access-Token";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Subject user = SecurityUtils.getSubject();

        String accessToken = requestContext.getHeaderString(AUTHENTICATION_HEADER);

        try {
            user.login(new AccessToken(accessToken));
            if (!user.isAuthenticated()) {
                throw new UnauthenticatedException();
            }
        } catch (AuthenticationException e) {
            requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
        }
    }

}
