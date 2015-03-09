package de.uni_stuttgart.riot.android.management;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> RIOT-87:Android:All changes of the last commits
import de.uni_stuttgart.riot.commons.rest.data.Storable;
=======
>>>>>>> RIOT-87:Android:Get things from the server
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.thing.ui.UIHint;
=======
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data

/**
 * Is the main abstract activity for all management classes.
 *
 * @author Benny
 */
public abstract class ManagementFragment extends Activity {

    // FIXME TODOs for this and its subclasses:
    // reorder methods
    // rename all fragment things to activity
    // check output messages
    // do all fixes and to-do-s
    // optimize the call of the "RIOTApiClient.getInstance()." -> maybe optimize that

    // FIXME Changes will be saved instant? (detailView)

    protected final String BUNDLE_OBJECT_ID = "SELECTED_OBJECT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutResource() == 0) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No layout resource was defined!");
            return;
        }

        setContentView(getLayoutResource());

        // Get extras from intent if saved instance state is empty
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        // Handle arguments if not null
        if (savedInstanceState != null) {
            handleArguments(savedInstanceState);
        }

        // Prepare the refresh button
        prepareMenuButtons();

        // Set the title of the frame
        updateTitle();

        // Refresh data (first time is needed to show data)
        doRefresh();

        // Helps to update the (action) menu
        // TODO setHasOptionsMenu(true);
        // for using onCreateOptionsMenu(), onPrepareOptionsMenu(),...
    }

    /**
     * Prepare the back button.
     */
    private void prepareMenuButtons() {
        ImageButton buttonBack = (ImageButton) findViewById(R.id.detail_button_back);
        if (buttonBack == null) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No back button was found!");
            return;
        }
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
        buttonBack.setVisibility(View.VISIBLE);

        ImageButton buttonRefresh = (ImageButton) findViewById(R.id.detail_button_refresh);
        if (buttonRefresh == null) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No refresh button was found!");
            return;
        }
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doOnRefreshClick();
            }
        });
        buttonRefresh.setVisibility(View.VISIBLE);
    }

    /**
     * Refresh the displayed data.
     */
    private void doRefresh() {
        // FIXME add ProcessDialog
//        // Prepare a progress dialog
//        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setTitle(getString(R.string.loading));
//        progressDialog.setMessage(getString(R.string.prepareData));

        new Thread() {

            @Override
            public void run() {
                // TODO use a timeout
                try {
                    // Get all known things (like a refresh action)
                    // ToDo use that on app start and with a refresh button
                    RIOTApiClient.getInstance().getDeviceBehavior().updateThings();

                    // Display the data in the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            // Show the progress dialog
//                            progressDialog.show();

                            // Display data
                            displayData();

                            // Update title if necessary
                            updateTitle();

//                            // Dismiss the progress dialog
//                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
//                    // Dismiss the progress dialog
//                    progressDialog.dismiss();

                    // Show a message that there was something wrong (FIXME output message!!)
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by creating view: " + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * Update the fragment title.
     */
    private void updateTitle() {
        setTitle(getPageTitle());
    }

    /**
     * Define what the refresh button will do.
     */
    protected abstract void doOnRefreshClick();

    /**
     * Handle the arguments on creating this view.
     *
     * @param bundle includes the arguments
     */
    protected abstract void handleArguments(Bundle bundle);

    /**
     * Returns the resource id of the view layout.
     *
     * @return the id of the resource layout
     */
    protected abstract int getLayoutResource();

    /**
     * Returns the string of the view title.
     *
     * @return the title
     */
    protected abstract String getPageTitle();

    /**
     * Checks if the item is an instance of this class.
     *
     * @param item the item that will be checked
     * @return true if the item is an instance of the class
     */
    protected abstract boolean isInstanceOf(Object item);

    /**
     * Build the elements that will be displayed.
     */
    protected abstract void displayData();

    /**
     * Returns the android resource id of the specific online state.
     *
     * @param onlineState that needs the resource id
     * @return the id of the online state resource
     */
    protected int getOnlineStateResourceId(OnlineState onlineState) {
        // FIXME -> push this method to another class??
        if (onlineState.equals(OnlineState.STATUS_ONLINE)) {
            return android.R.drawable.presence_online;
        } else if (onlineState.equals(OnlineState.STATUS_OFFLINE)) {
            return android.R.drawable.presence_offline;
        } else if (onlineState.equals(OnlineState.STATUS_AWAY)) {
            return android.R.drawable.presence_away;
        } else {
            return android.R.drawable.presence_busy;
        }
    }
}
