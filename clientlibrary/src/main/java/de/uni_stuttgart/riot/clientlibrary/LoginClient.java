package de.uni_stuttgart.riot.clientlibrary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.InternalRequest;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestExceptionWrapper;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

/**
 * Rest client for authentication handling.
 */
public class LoginClient {

    /** The Constant NOT_AUTH. */
    private static final int NOT_AUTH = 401;

    /** The Constant ERROR_THRESHOLD. */
    private static final int ERROR_THRESHOLD = 402;

    /** The Constant BAD_REQUEST. */
    private static final int BAD_REQUEST = 400;

    /** The Constant APPLICATION_JSON. */
    private static final String APPLICATION_JSON = "application/json";

    /** The Constant PREFIX. */
    private static final String PREFIX = "/api/v1/";

    /** The Constant LOGOUT_PATH. */
    private static final String LOGOUT_PATH = PREFIX + "auth/logout";

    /** The Constant LOGIN_PATH. */
    private static final String LOGIN_PATH = PREFIX + "auth/login";

    /** The Constant REFRESH_PATH. */
    private static final String REFRESH_PATH = PREFIX + "auth/refresh";

    /** The Constant ACCESS_TOKEN. */
    private static final String ACCESS_TOKEN = "Access-Token";

    /** The json mapper. */
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /** The token manager. */
    private TokenManager tokenManager;

    /** The device name. */
    private final String deviceName;

    /** The client. */
    private final HttpClient client = new DefaultHttpClient();

    /** The server url. */
    private final String serverUrl;

    /** The loged in. */
    private boolean logedIn;

    /**
     * Constructor.
     *
     * @param serverUrl
     *            the server url
     * @param deviceName
     *            the devicename
     * @param tokenManager
     *            the token manager
     */
    public LoginClient(String serverUrl, String deviceName, TokenManager tokenManager) {
        this.deviceName = deviceName;
        this.serverUrl = serverUrl;
        this.logedIn = false;
        this.tokenManager = tokenManager;
        this.jsonMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Gets the server url.
     *
     * @return the server url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Login for the given user, must be called before using any request.
     *
     * @param username
     *            .
     * @param password
     *            .
     * @return the user response
     * @throws RequestException
     *             Remote Exception
     * @throws ClientProtocolException .
     * @throws IOException .
     */
    public User login(String username, String password) throws RequestException, ClientProtocolException, IOException {
        return this.internalLogin(LOGIN_PATH, new LoginRequest(username, password));
    }

    /**
     * Refresh.
     *
     * @throws RequestException
     *             the request exception
     */
    void refresh() throws RequestException {
        RefreshRequest refreshRequest = new RefreshRequest(tokenManager.getRefreshToken());
        this.internalLogin(REFRESH_PATH, refreshRequest);
    }

    /**
     * Logout, invalidates the current tokens.
     * 
     * @throws RequestException
     *             Remote Exception
     * @throws JsonGenerationException .
     * @throws JsonMappingException .
     * @throws UnsupportedEncodingException .
     * @throws IOException .
     */
    public void logout() throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
        this.put(this.serverUrl + LOGOUT_PATH, "");
    }

    /**
     * Internal login.
     *
     * @param path
     *            the path
     * @param entity
     *            the entity
     * @return the user
     * @throws RequestException
     *             the request exception
     */
    User internalLogin(String path, Object entity) throws RequestException {
        try {
            HttpPut put = new HttpPut(serverUrl + path);
            put.setHeader(ACCESS_TOKEN, tokenManager.getAccessToken());
            StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
            jsonEntity.setContentType(APPLICATION_JSON);
            put.setEntity(jsonEntity);
            HttpResponse r = LoginClient.this.client.execute(put);
            int status = r.getStatusLine().getStatusCode();
            String mediaType = r.getEntity().getContentType().getValue();
            if (status >= BAD_REQUEST) {
                if (mediaType.equals(APPLICATION_JSON)) {
                    RequestExceptionWrapper error = LoginClient.this.jsonMapper.readValue(r.getEntity().getContent(), RequestExceptionWrapper.class);
                    error.throwIT();
                } else {
                    String result = EntityUtils.toString(r.getEntity());
                    RequestExceptionWrapper error = new RequestExceptionWrapper(status, result);
                    error.throwIT();
                }
            } else {
                AuthenticationResponse response = LoginClient.this.jsonMapper.readValue(r.getEntity().getContent(), AuthenticationResponse.class);
                tokenManager.setAccessToken(response.getAccessToken());
                tokenManager.setRefreshToken(response.getRefreshToken());
                this.logedIn = true;
                return response.getUser();
            }
        } catch (Exception e) {
            throw new RequestException(e);
        }
        return null;
    }

    /**
     * Put.
     *
     * @param target
     *            the target
     * @param entity
     *            the entity
     * @return the http response
     * @throws RequestException
     *             the request exception
     */
    public HttpResponse put(final String target, final Object entity) throws RequestException {
        try {
            return this.doRequest(new InternalRequest() {
                public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
                    HttpPut put = new HttpPut(target);
                    put.setHeader(ACCESS_TOKEN, tokenManager.getAccessToken());
                    StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
                    jsonEntity.setContentType(APPLICATION_JSON);
                    put.setEntity(jsonEntity);
                    return LoginClient.this.client.execute(put);
                }
            });
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Post.
     *
     * @param target
     *            the target
     * @param entity
     *            the entity
     * @return the http response
     * @throws RequestException
     *             the request exception
     */
    public HttpResponse post(final String target, final Object entity) throws RequestException {
        try {
            return this.doRequest(new InternalRequest() {
                public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
                    HttpPost post = new HttpPost(target);
                    post.setHeader(ACCESS_TOKEN, tokenManager.getAccessToken());
                    StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
                    jsonEntity.setContentType(APPLICATION_JSON);
                    post.setEntity(jsonEntity);
                    return LoginClient.this.client.execute(post);
                }
            });
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Delete.
     *
     * @param target
     *            the target
     * @return the http response
     * @throws RequestException
     *             the request exception
     */
    public HttpResponse delete(final String target) throws RequestException {
        try {
            return this.doRequest(new InternalRequest() {
                public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
                    HttpDelete delete = new HttpDelete(target);
                    delete.setHeader(ACCESS_TOKEN, tokenManager.getAccessToken());
                    return LoginClient.this.client.execute(delete);
                }
            });
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Get.
     *
     * @param target
     *            the target
     * @return the http response
     * @throws RequestException
     *             the request exception
     */
    public HttpResponse get(final String target) throws RequestException {
        try {
            return this.doRequest(new InternalRequest() {
                public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
                    HttpGet get = new HttpGet(target);
                    get.setHeader(ACCESS_TOKEN, tokenManager.getAccessToken());
                    return LoginClient.this.client.execute(get);
                }
            });
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Do request.
     *
     * @param r
     *            the r
     * @return the http response
     * @throws RequestException
     *             the request exception
     */
    HttpResponse doRequest(InternalRequest r) throws RequestException {
        try {
            HttpResponse response = r.doRequest();
            int status = response.getStatusLine().getStatusCode();
            String mediatype = "";
            if (response.getEntity() != null) {
                if (response.getEntity().getContentType() != null) {
                    mediatype = response.getEntity().getContentType().getValue();
                }
            }
            if (status >= ERROR_THRESHOLD || status == BAD_REQUEST) {
                if (mediatype.equals(APPLICATION_JSON)) {
                    RequestExceptionWrapper error = LoginClient.this.jsonMapper.readValue(response.getEntity().getContent(), RequestExceptionWrapper.class);
                    error.throwIT();
                } else {
                    String result = EntityUtils.toString(response.getEntity());
                    RequestExceptionWrapper error = new RequestExceptionWrapper(status, result);
                    error.throwIT();
                }
            }
            if (status == NOT_AUTH) {
                this.refresh();
                return response;
            } else {
                return response;
            }
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Checks if is loged in.
     *
     * @return true, if is loged in
     */
    public boolean isLogedIn() {
        return logedIn;
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    HttpClient getClient() {
        return client;
    }

    /**
     * Gets the json mapper.
     *
     * @return the json mapper
     */
    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }
}
