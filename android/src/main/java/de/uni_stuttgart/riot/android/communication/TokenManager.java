package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import de.uni_stuttgart.riot.android.account.AndroidUser;

/**
 * The Token Manager saves the tokens in a account using the AccountManager. This enables the Token Manager to restore the tokens, even if
 * the instance was destroyed.
 */
public class TokenManager implements de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager {

    /** Used as key for saving the access token in the account. */
    private static final String ACCESS_TOKEN = "accessToken";

    /** Used as key for saving the refresh token in the account. */
    private static final String REFRESH_TOKEN = "refreshToken";

    /** The access token. */
    private String accessToken;

    /** The refresh token. */
    private String refreshToken;

    /** The account manager. */
    private AccountManager accountManager;

    /** The account. */
    private Account account;

    /**
     * Instantiates a new token manager.
     *
     * @param context
     *            the parent activity
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public TokenManager(Context context) throws IOException {
        account = AndroidUser.getAccount(context);
        accountManager = AccountManager.get(context);

        try {
            if(account != null) {
                accessToken = accountManager.blockingGetAuthToken(account, ACCESS_TOKEN, false);
                refreshToken = accountManager.blockingGetAuthToken(account, REFRESH_TOKEN, false);
            }
        } catch (OperationCanceledException e) {
            throw new RuntimeException(e);
        } catch (AuthenticatorException e) {
            if (!(e.getCause() instanceof UnsupportedOperationException)) {
                throw new RuntimeException(e);
            }
            // UnsupportedOperationException gets thrown if no tokens are saved in the account yet, so ignore it
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#getAccessToken()
     */
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#setAccessToken(java.lang.String)
     */
    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        accountManager.setAuthToken(account, ACCESS_TOKEN, accessToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#getRefreshToken()
     */
    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#setRefreshToken(java.lang.String)
     */
    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        accountManager.setAuthToken(account, REFRESH_TOKEN, accessToken);
    }
}
