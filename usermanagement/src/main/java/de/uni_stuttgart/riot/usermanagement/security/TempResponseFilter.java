package de.uni_stuttgart.riot.usermanagement.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * Temporary filter to enable cross-side-scripting. This is needed when two web servers are used. Should be removed in the future.
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
@PreMatching
public class TempResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseCtx) throws IOException {
        responseCtx.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseCtx.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseCtx.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        responseCtx.getHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Access-Token");
    }
}
