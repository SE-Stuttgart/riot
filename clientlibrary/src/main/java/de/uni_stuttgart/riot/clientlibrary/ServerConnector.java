package de.uni_stuttgart.riot.clientlibrary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

/**
 * A server connector executes HTTPS requests against the server using the Apache HTTP client.
 * 
 * @author Philipp Keck
 */
public final class ServerConnector {

    private static final String ACCESS_TOKEN_HEADER = "Access-Token";
    private static final String PUT_LOGIN = "auth/login";
    private static final String PUT_REFRESH = "auth/refresh";
    private static final String PUT_LOGOUT = "auth/logout";
    private static final String APPLICATION_JSON = "application/json";
    private static final int CONNECTION_TIMEOUT = 5000;

    private final TokenManager tokenManager;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private ConnectionInformation connectionInformation;
    private String baseURL;
    private HttpClient client;
    private User currentUser;

    /**
     * Creates a new ServerConnector. The caller will need to make sure that the TokenManager provides valid tokens.
     * 
     * @param connectionInformation
     *            Information for addressing the REST interface on the server.
     * @param tokenManager
     *            The manager that will manage login tokens.
     */
    public ServerConnector(ConnectionInformation connectionInformation, TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.connectionInformation = connectionInformation;
        this.baseURL = connectionInformation.getFullBaseURL();
        this.jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(config);
        if (connectionInformation.getProtocol().equals("https")) {
            builder = builder.setSSLSocketFactory(SSLHelper.createSLLSocketFactory());
        }
        this.client = builder.build();
    }

    /**
     * Creates a new ServerConnector.
     * 
     * @param connectionInformationProvider
     *            The provider that will first provide the connection information and later serve as the token manager.
     */
    public ServerConnector(ConnectionInformationProvider connectionInformationProvider) {
        this(connectionInformationProvider.getInformation(), connectionInformationProvider);
    }

    /**
     * Login for the given user. Many requests require that the user is logged in. This can either happen by the token manager providing the
     * tokens or by this login method. Hint: Do not use this on Android, it won't work.
     *
     * @param username
     *            The user name.
     * @param password
     *            The user's password.
     * @return The user that has been logged in.
     * @throws RequestException
     *             When the login request failed.
     * @throws IOException
     *             When a network error occured.
     * @throws UnauthenticatedException
     *             When the credentials were not accepted (this is a special kind of {@link RequestException}!)
     */
    public User login(String username, String password) throws IOException, RequestException, UnauthenticatedException {
        AuthenticationResponse response = executeLoginRequest(username, password);
        // Set refresh token before auth token! See TokenManager.setRefreshToken() for more information.
        tokenManager.setRefreshToken(response.getRefreshToken());
        tokenManager.setAccessToken(response.getAccessToken());
        currentUser = response.getUser();
        return currentUser;
    }

    /**
     * Tries logging in with the given credentials, but only returns the result and does not store anything in the connector. This method is
     * separate because it cannot use the automated authentication recovery of {@link #execute(HttpUriRequest)}.
     * 
     * @param username
     *            The username.
     * @param password
     *            The password.
     * @return The response in case the login was successful.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws UnauthenticatedException
     *             When the credentials were not accepted.
     */
    public AuthenticationResponse executeLoginRequest(String username, String password) throws IOException, RequestException, UnauthenticatedException {
        return executeAuthenticationRequest(makePUTRequest(PUT_LOGIN, new LoginRequest(username, password)));
    }

    /**
     * Tries refreshing the login with the given refresh token, but only returns the result and does not store anything in the connector.
     * This method is separate because it cannot use the automated authentication recovery of {@link #execute(HttpUriRequest)}.
     * 
     * @param refreshToken
     *            The refresh token to use for authentication.
     * @return The response in case the refresh was successful.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws UnauthenticatedException
     *             When the request token was not accepted.
     */
    public AuthenticationResponse executeRefreshRequest(String refreshToken) throws IOException, RequestException, UnauthenticatedException {
        return executeAuthenticationRequest(makePUTRequest(PUT_REFRESH, new RefreshRequest(refreshToken)));
    }

    /**
     * Executes the given authentication request.
     * 
     * @param request
     *            The request.
     * @return The response.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws UnauthenticatedException
     *             When the authentication information was not accepted.
     */
    private AuthenticationResponse executeAuthenticationRequest(HttpPut request) throws IOException, RequestException, UnauthenticatedException {
        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return readResponseContent(response, AuthenticationResponse.class);
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new UnauthenticatedException("The server did not accept the password or refreshToken!");
        } else {
            throwPlainError(PUT_REFRESH, response);
            return null;
        }
    }

    /**
     * Logout, invalidates the current tokens.
     * 
     * @throws RequestException
     *             When the logout request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public void logout() throws IOException, RequestException {
        doPUT(PUT_LOGOUT, "");
        // Set refresh token before auth token! See TokenManager.setRefreshToken() for more information.
        tokenManager.setRefreshToken(null);
        tokenManager.setAccessToken(null);
        currentUser = null;
    }

    /**
     * Performs a GET request, returning the JSON result of the given type.
     * 
     * @param <T>
     *            The type of the result.
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param resultType
     *            The type of the result.
     * @return The parsed result entity of the request.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     * @throws NotFoundException
     *             When the target entity could not be found.
     */
    public <T> T doGET(String path, Class<T> resultType) throws IOException, RequestException, NotFoundException {
        HttpResponse response = execute(makeGETRequest(path));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            consume(response);
            throw new NotFoundException("Request to " + baseURL + path + " returned 404 Not Found");
        } else {
            return readResponseContent(checkForErrors(path, response), resultType);
        }
    }

    /**
     * Performs a GET request, returning the JSON result of the given type.
     * 
     * @param <T>
     *            The type of the result.
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param resultType
     *            The type of the result.
     * @return The parsed result entity of the request.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     * @throws NotFoundException
     *             When the target entity could not be found.
     */
    public <T> T doGET(String path, TypeReference<T> resultType) throws IOException, RequestException, NotFoundException {
        HttpResponse response = execute(makeGETRequest(path));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            consume(response);
            throw new NotFoundException("Request to " + baseURL + path + " returned 404 Not Found");
        } else {
            return readResponseContent(checkForErrors(path, response), resultType);
        }
    }

    /**
     * Performs a GET request, returning a collection of JSON results of the given type.
     * 
     * @param <T>
     *            The type of the result entries.
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param entryType
     *            The type of the result entries.
     * @return The parsed result entity of the request.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     * @throws NotFoundException
     *             When the target entity could not be found.
     */
    public <T> Collection<T> doGETCollection(String path, Class<T> entryType) throws IOException, RequestException, NotFoundException {
        HttpResponse response = execute(makeGETRequest(path));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            consume(response);
            throw new NotFoundException("Request to " + baseURL + path + " returned 404 Not Found");
        } else {
            return readResponseContent(checkForErrors(path, response), jsonMapper.getTypeFactory().constructCollectionType(Collection.class, entryType));
        }
    }

    /**
     * Performs a POST request, ignoring the result.
     * 
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param entity
     *            The entity to POST to the server.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     */
    public void doPOST(String path, Object entity) throws IOException, RequestException {
        consume(checkForErrors(path, execute(makePOSTRequest(path, entity))));
    }

    /**
     * Performs a POST request, returning the JSON result of the given type.
     * 
     * @param <T>
     *            The type of the result.
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param entity
     *            The entity to POST to the server.
     * @param resultType
     *            The type of the result.
     * @return The parsed result entity of the request.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     */
    public <T> T doPOST(String path, Object entity, Class<T> resultType) throws IOException, RequestException {
        return readResponseContent(checkForErrors(path, execute(makePOSTRequest(path, entity))), resultType);
    }

    /**
     * Performs a PUT request, ignoring the result.
     * 
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param entity
     *            The entity to POST to the server.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     */
    public void doPUT(String path, Object entity) throws IOException, RequestException {
        consume(checkForErrors(path, execute(makePUTRequest(path, entity))));
    }

    /**
     * Performs a PUT request, returning the JSON result of the given type.
     * 
     * @param <T>
     *            The type of the result.
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @param entity
     *            The entity to POST to the server.
     * @param resultType
     *            The type of the result.
     * @return The parsed result entity of the request.
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     */
    public <T> T doPUT(String path, Object entity, Class<T> resultType) throws IOException, RequestException {
        return readResponseContent(checkForErrors(path, execute(makePUTRequest(path, entity))), resultType);
    }

    /**
     * Performs a DELETE request.
     * 
     * @param path
     *            The path on the server (without server name, prefixes, etc.).
     * @throws RequestException
     *             When something went terribly wrong with the request.
     * @throws IOException
     *             When there is a network problem.
     */
    public void doDELETE(String path) throws IOException, RequestException {
        consume(checkForErrors(path, execute(makeDELETERequest(path))));
    }

    /**
     * Executes the given request.
     * 
     * @param request
     *            The request to execute.
     * @return The response. It is important to consume the content of this response in some way!
     * @throws IOException
     *             When a network error occured. Note that this method will first try to call the {@link ConnectionInformationProvider} for
     *             updating the server data before throwing this error.
     * @throws RequestException
     *             If something went terribly wrong.
     */
    public HttpResponse execute(HttpUriRequest request) throws IOException, RequestException {
        return execute(request, true, true, true);
    }

    /**
     * Executes the given request.
     * 
     * @param request
     *            The request to execute.
     * @param retryConnection
     *            Whether the request should be retried if a connection error occured.
     * @param refreshToken
     *            Whether the request should be retried after getting a new access token, if the old one was invalid.
     * @param retryLogin
     *            Whether the request should try to call the ConnectionInformationProvider for a relogin if it failed due to an
     *            authentication error.
     * @return The response.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             If something went terribly wrong.
     */
    private HttpResponse execute(HttpUriRequest request, boolean retryConnection, boolean refreshToken, boolean retryLogin) throws IOException, RequestException {

        // Add login information to the request.
        String accessToken = tokenManager.getAccessToken();
        request.setHeader(ACCESS_TOKEN_HEADER, accessToken);

        try {

            // Execute the request.
            HttpResponse response = client.execute(request);

            // Check if something went wrong that can be fixed here.
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                currentUser = null;
                consume(response);

                // The server did not like the access token. Maybe it expired or was empty. Try to refresh it.
                // If refresh it not possible, try relogin (with the user).
                if (refreshToken && tryRefresh()) {
                    return execute(request, false, false, retryLogin);
                } else if (retryLogin && tryRelogin()) {
                    return execute(request, false, false, false);
                } else {
                    throw new UnauthenticatedException("Access token expired, refresh token is invalid and could not get new credentials!");
                }

            } else {
                return response;
            }

        } catch (ClientProtocolException e) {
            // This really shouldn't happen.
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RequestException("Request to " + request.getURI() + " failed", e);
        } catch (IOException e) {
            // We have connectivity problems.
            // Ask the provider to update the connection details.
            String oldBaseURL = baseURL;
            if (retryConnection && tryRefreshConnectionInformation()) {
                HttpUriRequest newRequest = replaceOldUri(request, oldBaseURL);
                return execute(newRequest, false, refreshToken, retryLogin);
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates a clone of <tt>oldRequest</tt> where <tt>oldUri</tt> is replaced with {@link #baseURL}.
     * 
     * @param oldRequest
     *            The old request.
     * @param oldUri
     *            The old URI.
     * @return The new request.
     */
    private HttpUriRequest replaceOldUri(HttpUriRequest oldRequest, String oldUri) {
        if (!(oldRequest instanceof HttpRequestBase)) {
            throw new IllegalArgumentException("This only works for the requests generated by this class!");
        }

        String oldTarget = ((HttpRequestBase) oldRequest).getURI().toString();
        if (!oldTarget.startsWith(oldUri)) {
            throw new IllegalArgumentException("The old URI " + oldTarget + " does not start with " + oldUri);
        }

        String newTarget = oldTarget.substring(oldUri.length());
        if (oldRequest instanceof HttpGet) {
            return makeGETRequest(newTarget);
        } else if (oldRequest instanceof HttpPost) {
            return makePOSTRequest(newTarget, ((HttpPost) oldRequest).getEntity());
        } else if (oldRequest instanceof HttpPut) {
            return makePUTRequest(newTarget, ((HttpPut) oldRequest).getEntity());
        } else if (oldRequest instanceof HttpDelete) {
            return makeDELETERequest(newTarget);
        } else {
            throw new IllegalArgumentException("Cannot clone old request " + oldRequest);
        }
    }

    /**
     * Tries to refresh the access token.
     * 
     * @return True if successful (new access token is available), false if the server does not want the refresh token either.
     * @throws RequestException
     *             If something went terribly wrong.
     * @throws IOException
     *             If some network error occured.
     */
    private boolean tryRefresh() throws RequestException, IOException {

        tokenManager.invalidateAccessToken();
        if (tokenManager instanceof ConnectionInformationProvider && ((ConnectionInformationProvider) tokenManager).handlesTokenRefresh()) {
            // The token manager handles the refresh, which means that the next call to getAccessToken will return the refreshed token.
            return true;
        }

        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            // Cannot refresh without refresh token.
            return false;
        }

        try {
            AuthenticationResponse result = executeRefreshRequest(refreshToken);
            // Set refresh token before auth token! See TokenManager.setRefreshToken() for more information.
            tokenManager.setRefreshToken(result.getRefreshToken());
            tokenManager.setAccessToken(result.getAccessToken());
            currentUser = result.getUser();
            return true;
        } catch (UnauthenticatedException e) {
            return false; // Token is not valid anymore.
        }
    }

    /**
     * Tries calling the ConnectionInformationProvider for updating the connection information.
     * 
     * @return True if successful. Either the old or the new information will be present, so the {@link #connectionInformation} will never
     *         be empty.
     */
    private boolean tryRefreshConnectionInformation() {
        if (!(tokenManager instanceof ConnectionInformationProvider)) {
            return false;
        }

        ConnectionInformation newInformation = ((ConnectionInformationProvider) tokenManager).getNewInformation(connectionInformation);
        if (newInformation == null || newInformation.equals(connectionInformation)) {
            return false;
        } else {
            this.connectionInformation = newInformation;
            this.baseURL = newInformation.getFullBaseURL();
            return true;
        }
    }

    /**
     * Refreshes the connection information by retrieving it from the {@link ConnectionInformationProvider}.
     * 
     * @return True if successful.
     */
    public boolean refreshConnectionInformation() {
        return tryRefreshConnectionInformation();
    }

    /**
     * Tries calling the ConnectionInformationProvider for updating the tokens.
     * 
     * @return True if successful.
     */
    private boolean tryRelogin() {
        if (tokenManager instanceof ConnectionInformationProvider) {
            return ((ConnectionInformationProvider) tokenManager).relogin(this);
        } else {
            return false;
        }
    }

    /**
     * Constructs a HTTP PUT request.
     */
    private HttpPut makePUTRequest(String path, Object entity) {
        HttpPut put = new HttpPut(baseURL + path);
        put.setEntity(makeEntity(entity));
        return put;
    }

    /**
     * Constructs a HTTP POST request.
     */
    private HttpPost makePOSTRequest(String path, Object entity) {
        HttpPost post = new HttpPost(baseURL + path);
        post.setEntity(makeEntity(entity));
        return post;
    }

    /**
     * Constructs a HTTP GET request.
     */
    private HttpGet makeGETRequest(String path) {
        return new HttpGet(baseURL + path);
    }

    /**
     * Constructs a HTTP DELETE request.
     */
    private HttpDelete makeDELETERequest(String path) {
        return new HttpDelete(baseURL + path);
    }

    /**
     * Constructs an HTTP entity for the Apache HTTP client that efficiently serializes the given object in JSON format directly into the
     * output stream (without creating further in-memory representations first).
     */
    private HttpEntity makeEntity(final Object entity) {
        EntityTemplate result = new EntityTemplate(new ContentProducer() {
            public void writeTo(OutputStream outstream) throws IOException {
                jsonMapper.writeValue(outstream, entity);
            }
        });
        result.setContentType(APPLICATION_JSON);
        return result;
    }

    /**
     * Throws an exception if the response indicates a HTTP error.
     */
    private HttpResponse checkForErrors(String uri, HttpResponse response) throws RequestException {
        if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
            throwPlainError(uri, response);
        }
        return response;
    }

    /**
     * Retrieves the error message (not JSON) from the reponse and throws an according exception.
     */
    private void throwPlainError(String uri, HttpResponse response) throws RequestException {
        StringBuilder builder = new StringBuilder();
        builder.append("Server returned ");
        builder.append(response.getStatusLine());
        builder.append(" for ");
        builder.append(baseURL);
        builder.append(uri);
        if (response.getEntity() != null) {
            builder.append(": ");
            try {
                builder.append(EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                builder.append("Reading the error response failed");
            }
        }
        throw new RequestException(builder.toString());
    }

    /**
     * Reads a JSON response efficiently by directly parsing the resulting stream.
     */
    private <T> T readResponseContent(HttpResponse response, Class<T> expectedType) throws RequestException, IOException {
        try {
            return jsonMapper.readValue(response.getEntity().getContent(), expectedType);
        } catch (JsonProcessingException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Reads a JSON response efficiently by directly parsing the resulting stream.
     */
    private <T> T readResponseContent(HttpResponse response, TypeReference<T> expectedType) throws RequestException, IOException {
        try {
            return jsonMapper.readValue(response.getEntity().getContent(), expectedType);
        } catch (JsonProcessingException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Reads a JSON response efficiently by directly parsing the resulting stream.
     */
    private <T> T readResponseContent(HttpResponse response, JavaType expectedType) throws RequestException, IOException {
        try {
            return jsonMapper.readValue(response.getEntity().getContent(), expectedType);
        } catch (JsonProcessingException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Consumes the entity, if present.
     */
    private void consume(HttpResponse response) {
        if (response.getEntity() != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                // Ignore this
            }
        }
    }

    /**
     * Gets the Jackson ObjectMapper.
     * 
     * @return The ObjectMapper for generating or parsing JSON.
     */
    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    /**
     * Gets the current user.
     * 
     * @return The current use, may be <tt>null</tt>.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user. This method should only be used if a successful login request using
     * {@link #executeLoginRequest(String, String)} has been performed and the user has been returned as a result of that and the new tokens
     * are already available.
     * 
     * @param user
     *            The user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

}
