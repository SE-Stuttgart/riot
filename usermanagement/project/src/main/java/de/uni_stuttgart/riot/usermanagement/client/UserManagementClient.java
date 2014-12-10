package de.uni_stuttgart.riot.usermanagement.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

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
    
    public void login(String username, String password){
        JSONObject loginData = new JSONObject().put("username", username).put("password", password);
        this.internalLogin(LOGIN_PATH, loginData);
    }
    
    private void refresh() {
        JSONObject loginData = new JSONObject().put(REFRESH_TOKEN_FIELD, this.currentRefreshToken);
        this.internalLogin(REFRESH_PATH, loginData);
    }
    
    public void logout(){
        //TODO
        this.logedIn = false;
    }
    
    private void internalLogin(String path,JSONObject entity){
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(this.serverUrl).path(path); 
        System.out.println(target.getUri());
        Response r = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity.toString(), MediaType.APPLICATION_JSON));
        String s =r.readEntity(String.class);
        System.out.println(s);
        JSONObject response = new JSONObject(s);
        this.currentAuthenticationToken = (String) response.get(ACCESS_TOKEN_FIELD);
        this.currentRefreshToken = (String) response.get(REFRESH_TOKEN_FIELD);
        this.logedIn = true;
    }
    
    public Response put(final WebTarget target,final String mediaType,final Entity entity){
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).put(entity);
            }
        });
    }
    
    
    public Response post(final WebTarget target,final String mediaType,final Entity entity){
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).post(entity);
            }
        });
    }
    
    public Response delete(final WebTarget target,final String mediaType){
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(mediaType).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).delete();
            }
        });
    }
    
    public Response get(final WebTarget target,final String applicationJson){
        return this.doRequest(new InternalRequest() {
            public Response doRequest() {
                return target.request(applicationJson).header(ACCESS_TOKEN, UserManagementClient.this.currentAuthenticationToken).get();            
            }
        });
    }
    
    public Response doRequest(InternalRequest r){
        Response response = r.doRequest();
        if(response.getStatus() == 401){
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