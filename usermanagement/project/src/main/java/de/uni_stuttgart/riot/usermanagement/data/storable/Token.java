package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * Holds the Token that is used to authenticate a user and the associated refresh token
 * that could be used to retrieve a new token without the need of submitting the (username,password) credentials again.  
 * @author Jonas Tangermann
 *
 */
public class Token implements Storable {

    private Long tokenID;
    private final Long userID;
    private final Timestamp issueTime;
    private final Timestamp expirationTime;
    private String tokenValue;
    private String refreshtokenValue;
    private boolean valid;

    public Timestamp getIssueTime() {
        return issueTime;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    public Token(Long tokenID, Long userID, String tokenValue, String refreshtokenValue, Timestamp issueTime, Timestamp expirationTime, boolean valid) {
        this.tokenID = tokenID;
        this.userID = userID;
        this.issueTime = issueTime;
        this.expirationTime = expirationTime;
        this.setRefreshtokenValue(refreshtokenValue);
        this.setTokenValue(tokenValue);
        this.setValid(valid);
    }
    
    public Token(Long userID, String tokenValue, String refreshtokenValue, Timestamp issueTime, Timestamp expirationTime, boolean valid) {
        this.tokenID = -1L;
        this.userID = userID;
        this.issueTime = issueTime;
        this.expirationTime = expirationTime;
        this.setRefreshtokenValue(refreshtokenValue);
        this.setTokenValue(tokenValue);
        this.setValid(valid);
    }

    @Override
    public long getId() {
        return this.tokenID;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> params = new LinkedList<SearchParameter>();
        params.add(new SearchParameter(SearchFields.TOKENVALUE, this.getTokenValue()));
        params.add(new SearchParameter(SearchFields.USERID, this.getUserID()));
        params.add(new SearchParameter(SearchFields.ISSUETIME, this.getIssueTime()));
        params.add(new SearchParameter(SearchFields.EXPIRATIONTIME, this.getExpirationTime()));
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

    public String getRefreshtokenValue() {
        return refreshtokenValue;
    }

    public void setRefreshtokenValue(String refreshtokenValue) {
        this.refreshtokenValue = refreshtokenValue;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + ((issueTime == null) ? 0 : issueTime.hashCode());
        result = prime * result + ((refreshtokenValue == null) ? 0 : refreshtokenValue.hashCode());
        result = prime * result + ((tokenID == null) ? 0 : tokenID.hashCode());
        result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
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
        if (refreshtokenValue == null) {
            if (other.refreshtokenValue != null)
                return false;
        } else if (!refreshtokenValue.equals(other.refreshtokenValue))
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Token [tokenID=");
        builder.append(tokenID);
        builder.append(", userID=");
        builder.append(userID);
        builder.append(", issueTime=");
        builder.append(issueTime);
        builder.append(", expirationTime=");
        builder.append(expirationTime);
        builder.append(", tokenValue=");
        builder.append(tokenValue);
        builder.append(", refreshtokenValue=");
        builder.append(refreshtokenValue);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public void setId(Long id) {
        this.tokenID =id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
