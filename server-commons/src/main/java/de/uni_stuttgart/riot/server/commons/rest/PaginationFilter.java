package de.uni_stuttgart.riot.server.commons.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * The Class PaginationFilter. Intercepts responses and modifies the header, when the response type is a paginated collection.
 */
@Provider
public class PaginationFilter implements ContainerResponseFilter {

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext,
     * javax.ws.rs.container.ContainerResponseContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object entity = responseContext.getEntity();
        if (entity != null && entity instanceof PaginatedCollection) {

            PaginatedCollection<?> coll = (PaginatedCollection<?>) entity;
            responseContext.getHeaders().add("total", coll.getTotal());
            responseContext.getHeaders().add("offset", coll.getOffset());
            responseContext.getHeaders().add("limit", coll.getLimit());
        }

    }
}
