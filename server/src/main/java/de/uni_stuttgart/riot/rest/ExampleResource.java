package de.uni_stuttgart.riot.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * An exemplary REST resource class.
 */
@Path("example")
public class ExampleResource {

    /**
     * Returns a plain string message.
     * 
     * @return a plain string message
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "a plaintext response.";
    }

}
