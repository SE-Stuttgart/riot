package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

public class LoginClient {

	private static final String APPLICATION_JSON = "application/json";

    private static final String PREFIX = "/riot/api/v1/";

	private static final String LOGOUT_PATH = PREFIX + "auth/logout";
	private static final String LOGIN_PATH =  PREFIX +"auth/login";
	private static final String REFRESH_PATH =  PREFIX +"auth/refresh";
	private static final String ACCESS_TOKEN = "Access-Token";
	private String currentAuthenticationToken;
	private String currentRefreshToken;
	private final String deviceName;
	protected final ObjectMapper jsonMapper = new ObjectMapper(); 
    private final HttpClient client = HttpClientBuilder.create().build();



	private final String serverUrl;
	private boolean logedIn;

	public LoginClient(String serverUrl, String deviceName) {
		this.deviceName = deviceName;
		this.currentAuthenticationToken = "noToken";
		this.currentRefreshToken = "noToken";
		this.serverUrl = serverUrl;
		this.logedIn = false;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void login(String username, String password) throws RequestException, ClientProtocolException, IOException {
		this.internalLogin(LOGIN_PATH, new LoginRequest(username,password));
	}

	void refresh() throws RequestException, ClientProtocolException, IOException {
		RefreshRequest refreshRequest = new RefreshRequest(this.currentRefreshToken);
		this.internalLogin(REFRESH_PATH, refreshRequest);
	}

	public void logout() throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {		
		this.put(this.serverUrl + LOGOUT_PATH, null);
	}

	void internalLogin(String path, Object entity) throws RequestException, ClientProtocolException, IOException {
		HttpPut put = new HttpPut(serverUrl + path);
        put.setHeader(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken);
        StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
        jsonEntity.setContentType(APPLICATION_JSON);
        put.setEntity(jsonEntity);
        HttpResponse r = LoginClient.this.client.execute(put);
		int status = r.getStatusLine().getStatusCode();
		String mediaType = r.getEntity().getContentType().getValue();
		if(status >= 400){
			if(mediaType.equals(APPLICATION_JSON)){
				RequestExceptionWrapper error = LoginClient.this.jsonMapper.readValue(r.getEntity().getContent(), RequestExceptionWrapper.class);
				error.throwIT();
			} else {
				String result = EntityUtils.toString(r.getEntity());
				RequestExceptionWrapper error = new RequestExceptionWrapper(status, result);	
				error.throwIT();
			}
		} else{
			AuthenticationResponse response = LoginClient.this.jsonMapper.readValue(r.getEntity().getContent(), AuthenticationResponse.class);
			this.currentAuthenticationToken = response.getAccessToken();
			this.currentRefreshToken = response.getRefreshToken();
			this.logedIn = true;
			System.out.println(this.currentAuthenticationToken);
		} 
	}

	HttpResponse put(final String target, final Object entity) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
	    return this.doRequest(new InternalRequest() {
			public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
				System.out.println(target);
				HttpPut put = new HttpPut(target);
				put.setHeader(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken);
				StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
				jsonEntity.setContentType(APPLICATION_JSON);
                put.setEntity(jsonEntity);
				return LoginClient.this.client.execute(put);
			}
		});
	}

	HttpResponse post(final String target, final Object entity) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
		return this.doRequest(new InternalRequest() {
			public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
				HttpPost post = new HttpPost(target);
				post.setHeader(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken);
                StringEntity jsonEntity = new StringEntity(LoginClient.this.jsonMapper.writeValueAsString(entity));
                jsonEntity.setContentType(APPLICATION_JSON);
                post.setEntity(jsonEntity);
                return LoginClient.this.client.execute(post);
			}
		});
	}

	HttpResponse delete(final String target) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
		return this.doRequest(new InternalRequest() {
			public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
				HttpDelete delete = new HttpDelete(target);
				delete.setHeader(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken);
				return LoginClient.this.client.execute(delete);
			}
		});
	}

	HttpResponse get(final String target) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
		return this.doRequest(new InternalRequest() {
			public HttpResponse doRequest() throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
				HttpGet get = new HttpGet(target);
				get.setHeader(ACCESS_TOKEN, LoginClient.this.currentAuthenticationToken);
				return LoginClient.this.client.execute(get);
			}
		});
	}

	HttpResponse doRequest(InternalRequest r) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException {
	    HttpResponse response = r.doRequest();
        int status = response.getStatusLine().getStatusCode();
        String mediatype = response.getEntity().getContentType().getValue();
        if(status >= 402){
            if(mediatype.equals(APPLICATION_JSON)){
                RequestExceptionWrapper error = LoginClient.this.jsonMapper.readValue(response.getEntity().getContent(), RequestExceptionWrapper.class);
                error.throwIT();
            } else {
                String result = EntityUtils.toString(response.getEntity());
                RequestExceptionWrapper error = new RequestExceptionWrapper(status, result);
                error.throwIT();
            }
        }
        if (status == 401) {
            this.refresh();
            return response;
        } else {
            return response;
        }
	}

	public boolean isLogedIn() {
		return logedIn;
	}

	HttpClient getClient() {
		return HttpClientBuilder.create().build();
	}
}
