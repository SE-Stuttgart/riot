package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;

/**
 * Wrapper around a {@link Token}.<br>
 * FIXME These wrapper classes should not be necessary. Instead, use JAXB
 * annotations to tell, which attributes are to be sent to the client.
 * CHECKSTYLE:OFF
 */
public class TokenResponse {

	private Token token;

	/**
	 * Default-Constructor for JAXB.
	 */
	public TokenResponse() {
	}

	/**
	 * Creates a new TokenResponse.
	 *
	 * @param token
	 *            The wrapped Ttoken.
	 */
	public TokenResponse(Token token) {
		this.token = token;
	}

	public Long getId() {
		return token.getId();
	}

	public void setId(Long id) {
		token.setId(id);
	}

	public Timestamp getIssueTime() {
		return token.getIssueTime();
	}

	public Timestamp getExpirationTime() {
		return token.getExpirationTime();
	}

	public String getTokenValue() {
		return token.getTokenValue();
	}

	public void setTokenValue(String tokenValue) {
		token.setTokenValue(tokenValue);
	}

	public String getRefreshtokenValue() {
		return token.getRefreshtokenValue();
	}

	public void setRefreshtokenValue(String refreshtokenValue) {
		token.setRefreshtokenValue(refreshtokenValue);
	}

	public boolean isValid() {
		return token.isValid();
	}

	public void setValid(boolean valid) {
		this.setValid(valid);
	}

}
