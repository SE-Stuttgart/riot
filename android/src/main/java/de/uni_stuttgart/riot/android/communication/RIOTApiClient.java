package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;

import android.accounts.Account;
import android.content.Context;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.server.client.ConfigurationClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;

/**
 * Client for communicating with the RIOT API. It is not allowed to use this client in the main thread.
 *
 * @author Niklas Schnabel
 */
public class RIOTApiClient {

    /** The Constant API_URL. */
    private static final String API_URL = "https://belgrad.informatik.uni-stuttgart.de:8181/riot"; // TODO url, 10.0.2.2 is localhost for the
                                                                                              // android emulator

    /** The instance. */
    private static RIOTApiClient instance;

    /** The init. */
    private boolean init;

    /** The login client. */
    private LoginClient loginClient;

    /** The user management client. */
    private UsermanagementClient userManagementClient;

    /** The configuration client. */
    private ConfigurationClient configurationClient;

    /**
     * Instantiates a new RIOT api client.
     */
    private RIOTApiClient() {
    }

    /**
     * Gets the single instance of RIOTApiClient. It is <b>NOT</b> allowed to use the RIOTApiClient in the main thread!
     *
     * @return single instance of RIOTApiClient
     */
    public static RIOTApiClient getInstance() {
        if (instance == null) {
            instance = new RIOTApiClient();
        }
        return instance;
    }

    /**
     * Initializes the API Client. Has to be called before first usage or a Runtime Exception will be thrown.
     *
     * @param context
     *            the activity
     * @param deviceName
     *            the device name
     * @param account
     *            the android account of the user for which the api is created
     */
    public void init(Context context, String deviceName, Account account) {
        throwExceptionIfOnMainThread();

        try {
            loginClient = new LoginClient(API_URL, deviceName, new TokenManager(context, account));
        } catch (IOException e) {
            //CHECKSTYLE: OFF
            e.printStackTrace();
            //CHECKSTYLE: ON
            // FIXME use NotificationManagers
        }
        userManagementClient = new UsermanagementClient(loginClient);
        configurationClient = new ConfigurationClient(loginClient);

        init = true;
    }

    /**
     * Gets the login client.
     *
     * @return the login client
     */
    public LoginClient getLoginClient() {
        throwExceptionIfOnMainThread();
        checkIfInit();
        return loginClient;
    }

    /**
     * Gets the user management client.
     *
     * @return the user management client
     */
    public UsermanagementClient getUserManagementClient() {
        throwExceptionIfOnMainThread();
        checkIfInit();
        return userManagementClient;
    }

    /**
     * Gets the configuraton client.
     *
     * @return the configuraton client
     */
    public ConfigurationClient getConfiguratonClient() {
        throwExceptionIfOnMainThread();
        checkIfInit();
        return configurationClient;
    }

    /**
     * Check if init.
     */
    private void checkIfInit() {
        if (!init) {
            throw new RuntimeException("RiotApiClient has to be initialized before first usage. Call init() first!");
        }
    }

    /**
     * Throw exception if the current thread is the main thread.
     */
    private void throwExceptionIfOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
//TODO why do we need to throw this exeception?
//      if we want to throw it we should not check that this works from an async task! (where the code is also executed in the main thread apparently
//            throw new NetworkOnMainThreadException();
        }
    }
}
