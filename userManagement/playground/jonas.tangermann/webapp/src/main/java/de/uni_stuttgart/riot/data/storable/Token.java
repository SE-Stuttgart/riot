package de.uni_stuttgart.riot.data.storable;

import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;

public class Token implements Storable {
	
	private final Long tokenID;
	private final Long userID;
	private final Date issueTime;
	public Date getIssueTime() {
		return issueTime;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	private final Date expirationTime;
	private String tokenValue;
	
	public Token(Long tokenID,Long userID, String tokenValue, Date issueTime, Date expirationTime) {
		this.tokenID = tokenID;
		this.userID = userID;
		this.issueTime = issueTime;
		this.expirationTime = expirationTime;
		this.setTokenValue(tokenValue);
	}
	
	@Override
	public long getID() {
		return this.tokenID;
	}

	@Override
	public Collection<String> getSearchParam() {
		LinkedList<String> params = new LinkedList<String>();
		params.add(this.getTokenValue());
		return params;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Long getUserID() {
		return userID;
	}

}
