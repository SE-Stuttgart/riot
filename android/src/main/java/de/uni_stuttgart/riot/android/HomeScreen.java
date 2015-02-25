package de.uni_stuttgart.riot.android;

import android.app.Activity;
import android.os.Bundle;
import de.uni_stuttgart.riot.android.language.Language;

/**
 * This is the new MainAcitity which starts the other activitys.
 */
// CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class HomeScreen extends Activity {

    /**
     * onCreate method
     * 
     * @param savedInstanceState
     *            the last states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database stuff
        // this.deleteDatabase("Database");

        Language.setLanguage(this);

        setContentView(new DrawCanvas(this));
        // Initialize the API client. Initialization is not allowed in the main
        // thread.
        /*
         * final HomeScreen inst = this; new Thread() {
         * 
         * @Override public void run() { RIOTApiClient.getInstance().init(inst, "androidApp"); try {
         * System.out.println(RIOTApiClient.getInstance().getUserManagementClient().getRoles().size()); } catch (RequestException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); }
         * 
         * } }.start();
         */

    }

}
