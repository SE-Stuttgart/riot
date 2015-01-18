package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * The User class holds all basic information regarding to a user for the user management.
 *
 * @author Jonas Tangermann
 *
 */
public class UMUser extends User {

    /** The hashed password. */
    private String hashedPassword;

    /** The password salt. */
    private String passwordSalt;

    /** The hash iterations. */
    private int hashIterations;

    /** The login attemp count. */
    private int loginAttemptCount;

    /**
     * Instantiates a new UM user.
     */
    public UMUser() {
        super();
    }

    /**
     * Instantiates a new UM user.
     *
     * @param id
     *            the id
     * @param username
     *            the username
     * @param hashedPassword
     *            the hashed password
     * @param passwordSalt
     *            the password salt
     * @param hashIterations
     *            the hash iterations
     */
    public UMUser(long id, String username, String hashedPassword, String passwordSalt, int hashIterations) {
        super(id, username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
        this.setLoginAttemptCount(0);
    }

    /**
     * Instantiates a new UM user.
     *
     * @param id
     *            the id
     * @param username
     *            the username
     * @param hashedPassword
     *            the hashed password
     * @param passwordSalt
     *            the password salt
     * @param hashIterations
     *            the hash iterations
     * @param loginAttemptCount
     *            the login attemp count
     */
    public UMUser(long id, String username, String hashedPassword, String passwordSalt, int hashIterations, int loginAttemptCount) {
        super(id, username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
        this.setLoginAttemptCount(loginAttemptCount);
    }

    /**
     * Instantiates a new UM user.
     *
     * @param username
     *            the username
     * @param hashedPassword
     *            the hashed password
     * @param passwordSalt
     *            the password salt
     * @param hashIterations
     *            the hash iterations
     * @param loginAttemptCount
     *            the login attemp count
     */
    public UMUser(String username, String hashedPassword, String passwordSalt, int hashIterations, int loginAttemptCount) {
        super(username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
        this.setLoginAttemptCount(loginAttemptCount);
    }

    /**
     * Instantiates a new UM user.
     *
     * @param username
     *            the username
     * @param hashedPassword
     *            the hashed password
     * @param passwordSalt
     *            the password salt
     * @param hashIterations
     *            the hash iterations
     */
    public UMUser(String username, String hashedPassword, String passwordSalt, int hashIterations) {
        super(username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
        this.setLoginAttemptCount(0);
    }

    /**
     * Instantiates a new UM user.
     *
     * @param username
     *            the username
     */
    public UMUser(String username) {
        super(username);
        this.setHashedPassword(null);
        this.setPasswordSalt(null);
        this.setHashIterations(-1);
        this.setLoginAttemptCount(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [id=");
        builder.append(this.getId());
        builder.append(", username=");
        builder.append(username);
        builder.append(", hashedPassword=");
        builder.append(hashedPassword);
        builder.append(", passwordSalt=");
        builder.append(passwordSalt);
        builder.append(", hashIterations=");
        builder.append(hashIterations);
        builder.append(", loginAttemptCount=");
        builder.append(loginAttemptCount);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Gets the hashed password.
     *
     * @return the hashed password
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Sets the hashed password.
     *
     * @param hashedPassword
     *            the new hashed password
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Gets the password salt.
     *
     * @return the password salt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * Sets the password salt.
     *
     * @param passwordSalt
     *            the new password salt
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * Gets the hash iterations.
     *
     * @return the hash iterations
     */
    public int getHashIterations() {
        return hashIterations;
    }

    /**
     * Sets the hash iterations.
     *
     * @param hashIterations
     *            the new hash iterations
     */
    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    /**
     * Gets the login attemp count.
     *
     * @return the login attemp count
     */
    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    /**
     * Sets the login attemp count.
     *
     * @param loginAttempCount
     *            the new login attemp count
     */
    public void setLoginAttemptCount(int loginAttempCount) {
        this.loginAttemptCount = loginAttempCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + hashIterations;
        result = prime * result + ((hashedPassword == null) ? 0 : hashedPassword.hashCode());
        result = prime * result + loginAttemptCount;
        result = prime * result + ((passwordSalt == null) ? 0 : passwordSalt.hashCode());
        return result;
    }

    // CHECKSTYLE:OFF Auto-Generated Code
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
        UMUser other = (UMUser) obj;
        if (hashIterations != other.hashIterations) {
            return false;
        }
        if (hashedPassword == null) {
            if (other.hashedPassword != null) {
                return false;
            }
        } else if (!hashedPassword.equals(other.hashedPassword)) {
            return false;
        }
        if (loginAttemptCount != other.loginAttemptCount) {
            return false;
        }
        if (passwordSalt == null) {
            if (other.passwordSalt != null) {
                return false;
            }
        } else if (!passwordSalt.equals(other.passwordSalt)) {
            return false;
        }
        return true;
    }

}
