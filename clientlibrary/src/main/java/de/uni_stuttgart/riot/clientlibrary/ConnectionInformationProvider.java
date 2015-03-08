package de.uni_stuttgart.riot.clientlibrary;

/**
 * A connection information provider is able to store and provide information that are necessary to make and authenticate connections to the
 * server.
 * 
 * @author Philipp Keck
 */
public interface ConnectionInformationProvider extends TokenManager {

    /**
     * Gets the (possibly cached/stored) information for making a server connection.
     * 
     * @return The information object.
     */
    ConnectionInformation getInformation();

    /**
     * Calling this method indicates that something with the current information is wrong. This method asks the user to inspect the
     * information and correct it.
     * 
     * @param oldInformation
     *            The old (possibly wrong) information object.
     * @return The information object. May return <tt>null</tt> if unsuccessful.
     */
    ConnectionInformation getNewInformation(ConnectionInformation oldInformation);

    /**
     * Determines if the connection informatin provider handles token refreshes in its {@link #getAccessToken()} method. If
     * {@link #handlesTokenRefresh()} returns true, the methods {@link #invalidateAccessToken()} and {@link #getAccessToken()} are called to
     * generate a new token. Thus, the token manager must ensure that {@link #getAccessToken()} generates a new token in this case. If, on
     * the otherhand, {@link #handlesTokenRefresh()} returns false, {@link #getRefreshToken()} will be called and the result will be used to
     * generate new tokens, which are then stored normally in the token manager.
     * 
     * @return True if the manager handles the token refreshes.
     */
    boolean handlesTokenRefresh();

    /**
     * Calling this method indicates that something with the current authentication information is wrong. The client is not able to
     * authenticate with the server using its tokens, anymore, so a new login is necessary. This method prompts the user for his credentials
     * and tries authenticating to the server with them. If successful, it stores the new tokens in this TokenManager and returns true.
     * 
     * @param serverConnector
     *            The connector, can be used to login.
     * @return True if successful. This means that there are new tokens available now.
     */
    boolean relogin(ServerConnector serverConnector);

}
