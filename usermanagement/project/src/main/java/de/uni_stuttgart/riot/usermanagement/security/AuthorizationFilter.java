package de.uni_stuttgart.riot.usermanagement.security;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;

/**
 * The AuthorizationFilter will be used together with the {@link AuthorizationFilterBinding} class to enable the authorization annotations
 * of shiro to be usable as part of the JAX-RS implementation of jersey. The class will hook into the request call of a HTTP request and
 * grant or deny access based on the roles, permissions and actions of a subject (user).
 * 
 * @param <T> 
 * @author Marcel Lehwald
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter<T extends Annotation> implements ContainerRequestFilter {

    private final T authzSpec;
    private final AuthorizingAnnotationHandler handler;

    /**
     * Create the AuthorizationFilter.
     * 
     * @param authzSpec  
     * @param handler  
     */
    public AuthorizationFilter(T authzSpec, AuthorizingAnnotationHandler handler) {
        this.authzSpec = authzSpec;
        this.handler = handler;
    }

    /**
     *  Creates an AuthorizationFilter.
     *  
     * @param authzSpec 
     * @param <T> 
     * @return Returns an AuthorizationFilter.
     */
    public static <T extends Annotation> AuthorizationFilter<T> valueOf(T authzSpec) {
        return new AuthorizationFilter<T>(authzSpec, defaultHandler(authzSpec));
    }

    private static AuthorizingAnnotationHandler defaultHandler(Annotation annotation) {
        Class<?> t = annotation.annotationType();

        if (RequiresPermissions.class.equals(t)) {
            return new PermissionAnnotationHandler();
        } else if (RequiresRoles.class.equals(t)) {
            return new RoleAnnotationHandler();
        } else {
            throw new IllegalArgumentException("No default handler known for annotation " + t);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            handler.assertAuthorized(authzSpec);
        } catch (UnauthorizedException e) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        } catch (Exception e) {
            e.printStackTrace();
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }
}
