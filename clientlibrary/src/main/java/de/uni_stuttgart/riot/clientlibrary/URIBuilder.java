package de.uni_stuttgart.riot.clientlibrary;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Builder for URIs with parameters. This does not have as many features as most other URIBuilders, but it is availble without special
 * dependencies to Android or newer versions of HttpClient.
 * 
 * @author Philipp Keck
 */
public class URIBuilder {

    private final StringBuilder builder;

    /**
     * Constructs an URI with parameters only (i.e. it will start at the <tt>?</tt>).
     */
    public URIBuilder() {
        builder = new StringBuilder("?");
    }

    /**
     * Constructs an URI with the given base URL.
     * 
     * @param baseURL
     *            The base URL.
     */
    public URIBuilder(String baseURL) {
        builder = new StringBuilder(baseURL);
        builder.append("?");
    }

    /**
     * Adds a request parameter to the URI. A parameter may be added multiple times to create arrays.
     * 
     * @param parameterName
     *            The name of the parameter.
     * @param parameterValue
     *            The value of the parameter.
     */
    public void addParameter(String parameterName, String parameterValue) {
        try {
            builder.append(URLEncoder.encode(parameterName, "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(parameterValue, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
