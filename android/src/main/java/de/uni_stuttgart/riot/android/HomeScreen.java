package de.uni_stuttgart.riot.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * This is the new MainAcitity which starts the other activitys.
 */

public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise the InstanceManager for the message/notifications
        IM.INSTANCES.setActivity(this);

        // Database stuff
        // this.deleteDatabase("Database");

        DatabaseAccess.setDatabase(new RIOTDatabase(this));
        HomeScreenCanvas canvas = new HomeScreenCanvas(this);

        Language.setLanguage(this);

        setContentView(canvas);

        Log.v("HomeScreen", "new main activity.");
        new ActivityServerConnection<User>(this) {
            @Override
            protected User executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    Log.v("HomeScreen", "getting current user.");
                    return new UsermanagementClient(serverConnector).getCurrentUser();
                } catch (NotFoundException e) {
                    // if '/users/self' does not exist, there is sth wrong with the server and we should close the app
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onSuccess(User result) {
                // show username
                Log.v("HomeScreen", "current user: " + result.getUsername());
                IM.INSTANCES.getMH().showMessage("Logged in with: " + result.getUsername());
            }
        }.execute();

        // // Initialize the API client. Initialization is not allowed in the main
        // // thread.
        // final HomeScreen inst = this;
        // new Thread() {
        //
        // @Override
        // public void run() {
        // RIOTApiClient.getInstance().init(inst, "androidApp");
        // try {
        // System.out.println(RIOTApiClient.getInstance().getUserManagementClient().getPermissions());
        // } catch (RequestException e) {
        // e.printStackTrace();
        // }
        //
        // }
        // }.start();

    }

}
