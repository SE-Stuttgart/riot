package de.uni_stuttgart.riot.userManagement.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessTokenAuthentication implements AuthenticationToken {

	private static final long serialVersionUID = 1L;
	String token;

	public AccessTokenAuthentication(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return getToken();
	}

	@Override
	public Object getCredentials() {
		return getToken();
	}

	public String getToken() {
		return token;
	}

}
