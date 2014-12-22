package de.uni_stuttgart.riot.usermanagement.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

/**
 * TODO: This needs to be refactored:
 * <ul>
 * <li>First, it shouldn't become a god class, so it should be split</li>
 * <li>It should not use manual JSON serialization. Instead, the Entry-classes from the server project need to be cleaned up (there are old
 * model classes, those need to be merged/deleted) and moved to the commons project. Maybe a refactored Java-package structure in the server project would be cood.</li>
 * <li>When they're in the commons project, the client can use them directly as JAX-WS entities (currently, manually serialized Strings are used, which is tedious).</li>
 * <li>If any JSON library is required, use Jackson! or something that is already integrated in Java6/Android.</li>
 * </ul>
 * 
 */
public class UserManagementClient {
    private static final String LOGIN_PATH = "api/v1/auth/login";
    private static final String REFRESH_PATH = "api/v1/auth/refresh";
    private static final String REFRESH_TOKEN_FIELD = "refreshToken";
    private static final String ACCESS_TOKEN_FIELD = "accessToken";
    private static final String ACCESS_TOKEN = "Access-Token";
    private String currentAuthenticationToken;
    private String currentRefreshToken;
    private final String deviceName;

    private final String serverUrl;
    private boolean logedIn;

    public UserManagementClient(String serverUrl, String deviceName) {
        this.deviceName = deviceName;
        this.currentAuthenticationToken = "noToken";
        this.currentRefreshToken = "noToken";
        this.serverUrl = serverUrl;
        this.logedIn = false;
    }

    public void login(String username, String password) {
    	this.internalLogin(LOGIN_PATH, new LoginRequest(username,password));
    }

    private void refresh() {
    	RefreshRequest refreshRequest = new RefreshRequest(this.currentRefreshToken);
        this.internalLogin(REFRESH_PATH, refreshRequest);
    }

    public void logout() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(this.serverUrl).path("api/v1/auth/logout");
        System.out.println(target.getUri());
        Response r = target.request().header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).get();
        String s = r.readEntity(String.class);
        System.out.println(s);
        this.logedIn = false;
    }

    private void internalLogin(String path, Object entity) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(this.serverUrl).path(path);
        System.out.println(target.getUri());
        Response r = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
        AuthenticationResponse response = r.readEntity(AuthenticationResponse.class);
        this.currentAuthenticationToken = response.getAccessToken();
        this.currentRefreshToken = response.getRefreshToken();
        this.logedIn = true;
    }

    public Response put(final WebTarget target, final String mediaType, final Entity entity) {
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).put(entity);
            }
        });
    }

    public Response post(final WebTarget target, final String mediaType, final Entity entity) {
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).post(entity);
            }
        });
    }

    public Response delete(final WebTarget target, final String mediaType) {
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).delete();
            }
        });
    }

    public Response get(final WebTarget target, final String applicationJson) {
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(applicationJson).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).get();
            }
        });
    }

    public Response doRequest(InternalRequest r) {
        Response response = r.doRequest();
        if (response.getStatus() == 401) {
            this.refresh();
            return r.doRequest();
        } else {
            return response;
        }
    }

    public boolean isLogedIn() {
        return logedIn;
    }
}
