package de.uni_stuttgart.riot.data.storable;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;

public class Token implements Storable {
	
	private final Long tokenID;
	private final Long userID;
	private final Timestamp issueTime;
	
	public Timestamp getIssueTime() {
		return issueTime;
	}

	public Timestamp getExpirationTime() {
		return expirationTime;
	}

	private final Timestamp expirationTime;
	private String tokenValue;
	
	public Token(Long tokenID,Long userID, String tokenValue, Timestamp issueTime, Timestamp expirationTime) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expirationTime == null) ? 0 : expirationTime.hashCode());
		result = prime * result
				+ ((issueTime == null) ? 0 : issueTime.hashCode());
		result = prime * result + ((tokenID == null) ? 0 : tokenID.hashCode());
		result = prime * result
				+ ((tokenValue == null) ? 0 : tokenValue.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		if (expirationTime == null) {
			if (other.expirationTime != null)
				return false;
		} else if (!expirationTime.equals(other.expirationTime))
			return false;
		if (issueTime == null) {
			if (other.issueTime != null)
				return false;
		} else if (!issueTime.equals(other.issueTime))
			return false;
		if (tokenID == null) {
			if (other.tokenID != null)
				return false;
		} else if (!tokenID.equals(other.tokenID))
			return false;
		if (tokenValue == null) {
			if (other.tokenValue != null)
				return false;
		} else if (!tokenValue.equals(other.tokenValue))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		StringBuilder builder = new StringBuilder();
		builder.append("Token [tokenID=");
		builder.append(tokenID);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", issueTime=");
		builder.append(format.format(this.getIssueTime()));
		builder.append(", expirationTime=");
		builder.append(format.format(this.getExpirationTime()));
		builder.append(", tokenValue=");
		builder.append(tokenValue);
		builder.append("]");
		return builder.toString();
	}

}
