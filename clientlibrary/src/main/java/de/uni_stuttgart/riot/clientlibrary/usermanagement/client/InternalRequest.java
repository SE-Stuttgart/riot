package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * FIXME Explain what this interface is for?
 */
public interface InternalRequest {

    /**
     * FIXME.
     * 
     * @return FIXME
     */
    HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException;

}
