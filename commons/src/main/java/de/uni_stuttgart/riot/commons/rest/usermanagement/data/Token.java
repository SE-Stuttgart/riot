package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Holds the Token that is used to authenticate a user and the associated refresh token that could be used to retrieve a new token without
 * the need of submitting the (username,password) credentials again. Attention, issueTime is set by database on insert
 *
 * @author Jonas Tangermann CHECKSTYLE:OFF
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
     *
     * @param tokenID
     *            .
     * @param userID
     *            .
     * @param tokenValue
     *            .
     * @param refreshtokenValue
     *            .
     * @param issueTime
     *            .
     * @param expirationTime
     *            .
     * @param valid
     *            .
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
     *
     * @param userID
     *            .
     * @param tokenValue
     *            .
     * @param refreshtokenValue
     *            .
     * @param issueTime
     *            .
     * @param expirationTime
     *            .
     * @param valid
     *            .
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

    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
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
        final int magicNumber1 = 1231;
        final int magicNumber2 = 1237;
        int result = super.hashCode();
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + ((issueTime == null) ? 0 : issueTime.hashCode());
        result = prime * result + ((refreshtokenValue == null) ? 0 : refreshtokenValue.hashCode());
        result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        result = prime * result + (valid ? magicNumber1 : magicNumber2);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Token other = (Token) obj;
        if (expirationTime == null && other.expirationTime != null) {
            return false;
        } else if (!expirationTime.equals(other.expirationTime)) {
            return false;
        } else if (issueTime == null && other.issueTime != null) {
            return false;
        } else if (!issueTime.equals(other.issueTime)) {
            return false;
        } else if (refreshtokenValue == null && other.refreshtokenValue != null) {
            return false;
        } else if (!refreshtokenValue.equals(other.refreshtokenValue)) {
            return false;
        } else if (tokenValue == null && other.tokenValue != null) {
            return false;
        } else if (!tokenValue.equals(other.tokenValue)) {
            return false;
        } else if (userID == null && other.userID != null) {
            return false;
        } else if (!userID.equals(other.userID)) {
            return false;
        } else if (valid != other.valid) {
            return false;
        }
        return true;
    }

}
