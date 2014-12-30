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

public class LoginClient {

	private static final String PREFIX = "/riot/api/v1/";

	private static final String LOGOUT_PATH = PREFIX + "auth/logout";
	private static final String LOGIN_PATH =  PREFIX +"auth/login";
	private static final String REFRESH_PATH =  PREFIX +"auth/refresh";
	private static final String ACCESS_TOKEN = "Access-Token";
	private String currentAuthenticationToken;
	private String currentRefreshToken;
	private final String deviceName;

	private final Client client;
	private final String serverUrl;
	private boolean logedIn;

	public LoginClient(String serverUrl, String deviceName) {
		this.deviceName = deviceName;
		this.currentAuthenticationToken = "noToken";
		this.currentRefreshToken = "noToken";
		this.serverUrl = serverUrl;
		this.logedIn = false;
		this.client = ClientBuilder.newClient();
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void login(String username, String password) throws RequestException {
		this.internalLogin(LOGIN_PATH, new LoginRequest(username,password));
	}

	void refresh() throws RequestException {
		RefreshRequest refreshRequest = new RefreshRequest(this.currentRefreshToken);
		this.internalLogin(REFRESH_PATH, refreshRequest);
	}

	public void logout() {
		WebTarget target = client.target(this.serverUrl).path(LOGOUT_PATH);
		System.out.println(target.getUri());
		Response r = target.request().header(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken).put(null);
		String s = r.readEntity(String.class);
		System.out.println(s);
		this.logedIn = false;
	}

	void internalLogin(String path, Object entity) throws RequestException {
		WebTarget target = client.target(this.serverUrl).path(path);
		System.out.println(target.getUri());
		Response r = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		if(r.getStatus() >= 400){
			if(r.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)){
				RequestExceptionWrapper error = r.readEntity(RequestExceptionWrapper.class);
				error.throwIT();
			} else {
				String result = r.readEntity(String.class);
				RequestExceptionWrapper error = new RequestExceptionWrapper(r.getStatus(), result);
				error.throwIT();
			}
		} else{
			AuthenticationResponse response = r.readEntity(AuthenticationResponse.class);
			this.currentAuthenticationToken = response.getAccessToken();
			this.currentRefreshToken = response.getRefreshToken();
			this.logedIn = true;
		} 
	}

	Response put(final WebTarget target, final Entity entity) throws RequestException {
		return this.doRequest(new InternalRequest() {
			public Response doRequest() {
				return target.request(MediaType.APPLICATION_JSON).header(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken).put(entity);
			}
		});
	}

	Response post(final WebTarget target, final Entity entity) throws RequestException {
		return this.doRequest(new InternalRequest() {
			public Response doRequest() {
				return target.request(MediaType.APPLICATION_JSON).header(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken).post(entity);
			}
		});
	}

	Response delete(final WebTarget target) throws RequestException {
		return this.doRequest(new InternalRequest() {
			public Response doRequest() {
				return target.request(MediaType.APPLICATION_JSON).header(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken).delete();
			}
		});
	}

	protected Response get(final WebTarget target) throws RequestException {
		return this.doRequest(new InternalRequest() {
			public Response doRequest() {
				return target.request(MediaType.APPLICATION_JSON).header(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken).get();
			}
		});
	}

	Response doRequest(InternalRequest r) throws RequestException {
		Response response = r.doRequest();
		System.out.println(response.getStatusInfo().toString());		
		if(response.getStatus() >= 402){
			if(response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)){
				RequestExceptionWrapper error = response.readEntity(RequestExceptionWrapper.class);
				error.throwIT();
			} else {
				String result = response.readEntity(String.class);
				RequestExceptionWrapper error = new RequestExceptionWrapper(response.getStatus(), result);
				error.throwIT();
			}
		}
		if (response.getStatus() == 401) {
			this.refresh();
			return response;
		} else {
			return response;
		}
	}

	public boolean isLogedIn() {
		return logedIn;
	}

	Client getClient() {
		return client;
	}

}
