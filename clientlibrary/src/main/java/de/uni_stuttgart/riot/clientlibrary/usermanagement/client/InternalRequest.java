package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Interface for encapsulation of put,get,post,delete requests. (Used in {@link LoginClient} to add equivalent behavior to all requests)
 */
public interface InternalRequest {

    /**
     * Executes a http request.
     * 
     * @return {@link HttpResponse}
     * @throws JsonGenerationException .
     * @throws JsonMappingException .
     * @throws UnsupportedEncodingException .
     * @throws IOException .
     */
    HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException;

}
