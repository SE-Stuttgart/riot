package de.uni_stuttgart.riot.server.commons.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Passes exceptions that occured during the request both to the client and to SLF4J, so that it appears in many logfiles.
 * 
 * @author Philipp Keck
 */
@Provider
public class ExceptionLogger implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("toResponse() caught exception", exception);
        return Response.status(getStatusCode(exception)).entity(getEntity(exception)).build();
    }

    /**
     * 
     * Get appropriate HTTP status code for an exception.
     * 
     * @param exception
     *            The exception.
     * @return The HTTP status code.
     */
    private int getStatusCode(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse().getStatus();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    /**
     * Get response body for an exception.
     * 
     * @param exception
     *            The exception.
     * @return A HTTP entity that contains the exceptions's full stack trace in its body.
     */
    private Object getEntity(Throwable exception) {
        // return stack trace for debugging (should be deactivated for production use).
        StringWriter errorMsg = new StringWriter();
        exception.printStackTrace(new PrintWriter(errorMsg)); // NOCS
        return errorMsg.toString();
    }
}
