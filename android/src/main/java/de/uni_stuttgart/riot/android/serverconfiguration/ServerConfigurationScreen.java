package de.uni_stuttgart.riot.android.serverconfiguration;

import java.io.IOException;
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
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.client.ConfigurationClient;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;

/**
 * The Server Configuration Screen, with which the Administrator can change the settings of the server.
 */
public class ServerConfigurationScreen extends Activity implements EditConfigurationDialogFragment.EditConfigurationDialogListener {
    private static final String TAG = "ServerConfigurationScreen";

    private List<ConfigurationEntry> data = new ArrayList<ConfigurationEntry>();
    private ServerConfigurationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the title and icon of the action bar
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.serverConfiguration));
        actionBar.setIcon(R.drawable.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.server_setting_screen);
        displayData();
        retrieveRemoteData();
    }

    /**
     * Fills list view with server configuration data and add click listener to it.
     */
    private void displayData() {

        adapter = new ServerConfigurationAdapter(this, R.layout.server_setting_item, data);
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

    /**
     * Show edit configuration dialog.
     *
     * @param title
     *            the title
     * @param value
     *            the value
     * @param key
     *            the key
     */
    private void showEditConfigurationDialog(String title, String value, String key) {
        EditConfigurationDialogFragment editDialog = new EditConfigurationDialogFragment(title, value, key);
        editDialog.show(getFragmentManager(), "dialog");
    }

    /**
     * Retrieves server configuration.
     */
    private void retrieveRemoteData() {
        new ActivityServerConnection<List<ConfigurationEntry>>(this) {
            @Override
            protected List<ConfigurationEntry> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    Log.v(TAG, "getting current server configuration.");
                    return (List<ConfigurationEntry>) new ConfigurationClient(serverConnector).getConfiguration();
                } catch (RequestException e) {
                    Log.v(TAG, "Exception getting current server configuration.", e);
                    return null;
                }
            }

            @Override
            protected void onSuccess(List<ConfigurationEntry> result) {
                // IM.INSTANCES.getMH().showMessage("Logged in with: " + result.getUsername());
                Log.v(TAG, "Number of server configuration entries: " + result.size());
                adapter.clear();
                adapter.addAll(result);
                Log.v(TAG, "Number of server configuration entries at data: " + data.size());

            }
        }.execute();
    }

    /**
     * Updates configuration on server.
     */
    private void updateRemoteServerConfiguration(final String key, final String oldValue, final String newValue) {
        final int pos = getPosition(key);
        new ActivityServerConnection<ConfigurationEntry>(this) {
            @Override
            protected ConfigurationEntry executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    Log.v(TAG, "updating selected server configuration.");

                    ConfigurationEntry currentConfigEntry = data.get(pos);
                    currentConfigEntry.setConfigValue(newValue);
                    new ConfigurationClient(serverConnector).updateConfigurationEntry(currentConfigEntry.getId(), currentConfigEntry);
                    return currentConfigEntry;
                } catch (RequestException e) {
                    Log.v(TAG, "Exception updating server configuration.", e);
                    return null;
                }
            }

            @Override
            protected void onSuccess(ConfigurationEntry result) {
                // IM.INSTANCES.getMH().showMessage("Logged in with: " + result.getUsername());
                Log.v(TAG, "Updated configuration entry with id: " + result.getId());
                // updates config entry
                data.set(pos, result);
            }
        }.execute();
    }

    /**
     * Gets the position of key on the data list.
     *
     * @param keyName
     *            the key name
     * @return the position
     */
    private int getPosition(String keyName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getConfigKey().equalsIgnoreCase(keyName)) {
                return i;
            }
        }
        return -1;
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

    @Override
    public void onDialogSetClick(String key, String oldValue, String newValue) {
        updateRemoteServerConfiguration(key, oldValue, newValue);
    }
}
