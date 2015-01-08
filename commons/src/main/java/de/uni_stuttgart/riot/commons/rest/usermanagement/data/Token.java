package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Holds the Token that is used to authenticate a user and the associated refresh token
 * that could be used to retrieve a new token without the need of submitting the (username,password) credentials again.  
 * Attention, issueTime is set by database on insert
 * @author Jonas Tangermann
 *
 */
public class Token extends Storable {

    private Long userID;
    private Timestamp issueTime;
    private Timestamp expirationTime;
    private String tokenValue;
    private String refreshtokenValue;
    private boolean valid;

    /**
     * Constructor.
     */
    public Token() {
    }

    /**
     * Constructor.
     * @param tokenID .
     * @param userID .
     * @param tokenValue .
     * @param refreshtokenValue .
     * @param issueTime .
     * @param expirationTime .
     * @param valid .
     */
    public Token(Long tokenID, Long userID, String tokenValue, String refreshtokenValue, Timestamp issueTime, Timestamp expirationTime, boolean valid) {
        super(tokenID);
        this.userID = userID;
        this.issueTime = issueTime;
        this.expirationTime = expirationTime;
        this.setRefreshtokenValue(refreshtokenValue);
        this.setTokenValue(tokenValue);
        this.setValid(valid);
    }

    /**
     * Constructor.
     * @param userID .
     * @param tokenValue .
     * @param refreshtokenValue .
     * @param issueTime .
     * @param expirationTime .
     * @param valid .
     */
    public Token(Long userID, String tokenValue, String refreshtokenValue, Timestamp issueTime, Timestamp expirationTime, boolean valid) {
        super(-1L);
        this.userID = userID;
        this.issueTime = issueTime;
        this.expirationTime = expirationTime;
        this.setRefreshtokenValue(refreshtokenValue);
        this.setTokenValue(tokenValue);
        this.setValid(valid);
    }


    public Timestamp getIssueTime() {
        return issueTime;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + ((issueTime == null) ? 0 : issueTime.hashCode());
        result = prime * result + ((refreshtokenValue == null) ? 0 : refreshtokenValue.hashCode());
        result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        result = prime * result + (valid ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Token other = (Token) obj;
        if (expirationTime == null) {
            if (other.expirationTime != null) {
                return false;
            }
        } else if (!expirationTime.equals(other.expirationTime)) {
            return false;
        }
        if (issueTime == null) {
            if (other.issueTime != null) {
                return false;
            }
        } else if (!issueTime.equals(other.issueTime)) {
            return false;
        }
        if (refreshtokenValue == null) {
            if (other.refreshtokenValue != null) {
                return false;
            }
        } else if (!refreshtokenValue.equals(other.refreshtokenValue)) {
            return false;
        }
        if (tokenValue == null) {
            if (other.tokenValue != null) {
                return false;
            }
        } else if (!tokenValue.equals(other.tokenValue)) {
            return false;
        }
        if (userID == null) {
            if (other.userID != null) {
                return false;
            }
        } else if (!userID.equals(other.userID)) {
            return false;
        }
        if (valid != other.valid) {
            return false;
        }
        return true;
    }


}
