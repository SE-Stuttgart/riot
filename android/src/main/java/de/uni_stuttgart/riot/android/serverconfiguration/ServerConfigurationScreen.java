package de.uni_stuttgart.riot.android.serverconfiguration;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.server.client.ConfigurationClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;

/**
 * The Server Configuration Screen, with which the Administrator can change the settings of the server.
 */
public class ServerConfigurationScreen extends Activity implements EditConfigurationDialogFragment.EditConfigurationDialogListener {
    private static final String TAG = "ServerConfigurationScreen";

    // server configuration requests
    private static final String GET_CONFIGURATION = "GET_CONFIGURATION";
    private static final String UPDATE_CONFIGURATION = "UPDATE_CONFIGURATION";

    private ActionBar actionBar;
    private List<ConfigurationEntry> data = new ArrayList<ConfigurationEntry>();

    // private ServerConfigurationRequest serverConfigRequest = new ServerConfigurationRequest(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.server_setting_screen);

        // Sets the title and icon of the action bar
        actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.serverConfiguration));
        actionBar.setIcon(R.drawable.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        retrieveRemoteData();
        // serverConfigRequest.execute(GET_CONFIGURATION);

        displayData();
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see android.app.Activity#onDestroy()
    // */
    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    //
    // if (serverConfigRequest.getStatus() == Status.RUNNING) {
    // serverConfigRequest.cancel(true);
    // }
    // }

    // Local testing
    private void retrieveLocalData() {

        data.add(new ConfigurationEntry(ConfigurationKey.um_hashIterations, "200000", ConfigurationKey.um_hashIterations.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_authTokenValidTime, "10800", ConfigurationKey.um_authTokenValidTime.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_maxLoginRetries, "5", ConfigurationKey.um_maxLoginRetries.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_allowedSpecialChars, "][?\\/<~#`''!@$%^&§*()+-=}\"|:;,>{", ConfigurationKey.um_pwValidator_allowedSpecialChars.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_lowerCaseCharsCount, "1", ConfigurationKey.um_pwValidator_lowerCaseCharsCount.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_maxLength, "20", ConfigurationKey.um_pwValidator_maxLength.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_minLength, "6", ConfigurationKey.um_pwValidator_minLength.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_numberCount, "1", ConfigurationKey.um_pwValidator_numberCount.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_specialCharsCount, "1", ConfigurationKey.um_pwValidator_specialCharsCount.getValueType().getSimpleName()));
        data.add(new ConfigurationEntry(ConfigurationKey.um_pwValidator_upperCaseCharCount, "1", ConfigurationKey.um_pwValidator_upperCaseCharCount.getValueType().getSimpleName()));
    }

    /**
     * Fills list view with server configuration data.
     */
    private void displayData() {

        if (data.isEmpty()) {
            Toast.makeText(this, R.string.loadConfiguration_error, Toast.LENGTH_LONG).show();
            retrieveLocalData();
        }

        ServerConfigurationAdapter adapter = new ServerConfigurationAdapter(this, R.layout.server_setting_item, data);
        ListView listView = (ListView) findViewById(R.id.serverSettingListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // gets the subject
                String subject = ((TextView) view.findViewById(R.id.serverSettingItem_subject)).getText().toString();

                // gets the value
                String value = ((TextView) view.findViewById(R.id.serverSettingItem_value)).getText().toString();

                showEditConfigurationDialog(subject, value, data.get(position).getConfigKey());
            }
        });
    }

    private void showEditConfigurationDialog(String title, String value, String key) {
        EditConfigurationDialogFragment editDialog = new EditConfigurationDialogFragment(title, value, key);
        editDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogSetClick(String key, String oldValue, String newValue) {

        updateRemoteServerConfiguration(key, oldValue, newValue);
        // serverConfigRequest.execute(UPDATE_CONFIGURATION, key, newValue);

    }

    /**
     * Function to go back to the HomeScreen.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private int getPosition(String keyName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getConfigKey().equalsIgnoreCase(keyName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The AsycTask for the server communication for operations on its settings.
     */
    // class ServerConfigurationRequest extends AsyncTask<String, Integer, Long> {
    //
    // ServerConfigurationScreen serverConfigActivity;
    //
    // ServerConfigurationRequest(ServerConfigurationScreen serverConfigActivity) {
    // this.serverConfigActivity = serverConfigActivity;
    // }
    //
    // @Override
    // protected Long doInBackground(String... parameter) {
    //
    // if (parameter.length > 0) {
    // String request = parameter[0];
    // ConfigurationClient configClient = RIOTApiClient.getInstance().getConfiguratonClient();
    //
    // if (request == GET_CONFIGURATION) {
    //
    // try {
    // // updates data
    // data = (List<ConfigurationEntry>) configClient.getConfiguration();
    // Log.v(TAG, "getConfiguration succeed");
    // } catch (RequestException e) {
    // Log.v(TAG, "getConfiguration failed", e);
    // return -1L;
    // }
    //
    // } else if (request == UPDATE_CONFIGURATION && parameter.length == 3) {
    //
    // try {
    // String key = parameter[1];
    // String newValue = parameter[2];
    //
    // // retrieves current config entry from server
    // ConfigurationEntry currentConfigEntry = configClient.getConfigurationEntry(ConfigurationKey.valueOf(key));
    //
    // if (currentConfigEntry != null) {
    // Log.v(TAG, "found configuration key");
    //
    // if (currentConfigEntry.getConfigValue() != newValue) {
    // currentConfigEntry.setConfigValue(newValue);
    //
    // // updates config entry
    // configClient.updateConfigurationEntry(currentConfigEntry.getId(), currentConfigEntry);
    //
    // int pos = getPosition(key);
    // data.set(pos, currentConfigEntry);
    // }
    // }
    //
    // } catch (RequestException e) {
    // Log.v(TAG, "update configuration server failed", e);
    // }
    // }
    // }
    // return 0L;
    // }
    //
    // @Override
    // protected void onPostExecute(Long result) {
    //
    // if (result != 0) {
    // // Toast.makeText(serverConfigActivity, R.string.loadConfiguration_error, Toast.LENGTH_LONG).show();
    // }
    // }
    // }

    /**
     * Retrieves server configuration.
     */
    private void retrieveRemoteData() {
        Thread thread = new Thread() {
            @Override
            public void run() {

                ConfigurationClient configClient = RIOTApiClient.getInstance().getConfiguratonClient();
                try {
                    data = (List<ConfigurationEntry>) configClient.getConfiguration();
                    Log.v(TAG, "getConfiguration succeed");
                } catch (RequestException e) {
                    Log.v(TAG, "getConfiguration failed", e);
                }
            }
        };
        thread.start();

        // waits for thread to finish processing
        try {
            final int timeout = 1000;
            thread.join(timeout);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }

    /**
     * Updates configuration on server.
     */
    private void updateRemoteServerConfiguration(final String key, final String oldValue, final String newValue) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                ConfigurationClient configClient = RIOTApiClient.getInstance().getConfiguratonClient();
                try {
                    // retrieves current config entry from server
                    ConfigurationEntry currentConfigEntry = configClient.getConfigurationEntry(ConfigurationKey.valueOf(key));
                    if (currentConfigEntry != null) {
                        Log.v(TAG, "found configuration key");

                        if (currentConfigEntry.getConfigValue() != newValue) {
                            currentConfigEntry.setConfigValue(newValue);

                            // updates config entry
                            configClient.updateConfigurationEntry(currentConfigEntry.getId(), currentConfigEntry);

                            int pos = getPosition(key);
                            data.set(pos, currentConfigEntry);

                            // retrieves all entries from server TODO: activated when requests to server are working again
                            // data = (List<ConfigurationEntry>) configClient.getConfiguration();
                        }
                    }

                } catch (RequestException e) {
                    Log.v(TAG, "update configuration server failed", e);
                }
            }
        };
        thread.start();

        // waits for thread to finish processing
        try {
            final int timeout = 1000;
            thread.join(timeout);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }
}
