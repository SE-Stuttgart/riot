package de.uni_stuttgart.riot.android;

import android.app.Activity;
import android.os.Bundle;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;

/**
 * This is the new MainAcitity which starts the other activitys.
 */

public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database stuff
        // this.deleteDatabase("Database");

        DatabaseAccess.setDatabase(new RIOTDatabase(this));
        DrawCanvas canvas = new DrawCanvas(this);

        Language.setLanguage(this);

        setContentView(canvas);

        // Initialize the API client. Initialization is not allowed in the main
        // thread.

        final HomeScreen inst = this;
        new Thread() {

            @Override
            public void run() {
                RIOTApiClient.getInstance().init(inst, "androidApp");
                try {
                    System.out.println(RIOTApiClient.getInstance().getUserManagementClient().getRoles().size());
                } catch (RequestException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }
}
