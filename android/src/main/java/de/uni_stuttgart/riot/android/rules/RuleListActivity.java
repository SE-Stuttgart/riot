package de.uni_stuttgart.riot.android.rules;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.android.LoadingListAdapter;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.ResourceUtils;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.communication.ActivityServerTask;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.client.RuleClient;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * This activity displays a list of all rules that the user owns and allows to modify them or add new rules.
 * 
 * @author Philipp Keck
 */
public class RuleListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the title and icon of the action bar.
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.rules_rules));
        actionBar.setIcon(R.drawable.rules);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.rules_screen);

        // Add-button.
        Button btnAddRule = (Button) findViewById(R.id.btnAddRule);
        btnAddRule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doAddNewRule();
            }
        });

        registerForContextMenu(getListView());

    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadList();
    }

    private void reloadList() {
        // Load rules and eventually display them.
        setListAdapter(new LoadingListAdapter(this));
        new ActivityServerConnection<Collection<RuleConfiguration>>(this) {
            protected Collection<RuleConfiguration> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                return new RuleClient(serverConnector).getRules();
            }

            protected void onSuccess(Collection<RuleConfiguration> result) {
                setListAdapter(new RulesListAdapter(result.toArray(new RuleConfiguration[result.size()])));
            }
        }.execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        RuleConfiguration config = (RuleConfiguration) getListView().getItemAtPosition(position);
        doEditRule(config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.rules_btnAddRule).setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            doAddNewRule();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, R.string.edit);
        menu.add(0, 1, 1, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        switch (item.getItemId()) {
        case 0:
            doEditRule((RuleConfiguration) getListView().getItemAtPosition(position));
            return true;
        case 1:
            doDeleteRule((RuleConfiguration) getListView().getItemAtPosition(position));
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    /**
     * Shows a dialog for selecting a rule type and then displays the rule details activity for entering the rule details.
     */
    private void doAddNewRule() {
        new ActivityServerConnection<Collection<RuleDescription>>(this) {
            protected Collection<RuleDescription> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                return new RuleClient(serverConnector).getRuleDescriptions();
            }

            protected void onSuccess(Collection<RuleDescription> result) {

                final Map<String, RuleDescription> descriptions = new HashMap<String, RuleDescription>();
                for (RuleDescription description : result) {
                    String shortName = description.getType();
                    shortName = shortName.substring(shortName.lastIndexOf('.') + 1);
                    descriptions.put(shortName, description);
                }
                final String[] shortNames = descriptions.keySet().toArray(new String[0]);

                AlertDialog.Builder builder = new AlertDialog.Builder(RuleListActivity.this);
                builder.setTitle(R.string.rules_chooseType);
                builder.setItems(shortNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String shortName = shortNames[which];
                        RuleDescription description = descriptions.get(shortName);
                        doShowDetailActivity(description, null);
                    }
                });

                builder.create().show();

            }
        }.execute();
    }

    /**
     * Fetches the rule type description and then displays the rule detail activity for editing the given rule.
     * 
     * @param config
     *            The rule configuration.
     */
    private void doEditRule(final RuleConfiguration config) {
        new ActivityServerConnection<RuleDescription>(this) {
            protected RuleDescription executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    return new RuleClient(serverConnector).getRuleDescription(config.getType());
                } catch (NotFoundException e) {
                    IM.INSTANCES.getMH().showMessage("Internal error, rule type disappeared");
                    throw new RuntimeException(e);
                }
            }

            protected void onSuccess(RuleDescription description) {
                doShowDetailActivity(description, config);
            }
        }.execute();
    }

    /**
     * Displays the rule detail activity.
     * 
     * @param description
     *            The rule type description. Mandatory.
     * @param config
     *            The rule configuration. If this is <tt>null</tt>, a new rule instance will be created.
     */
    private void doShowDetailActivity(RuleDescription description, RuleConfiguration config) {
        Intent intent = new Intent(this, RuleDetailActivity.class);
        intent.putExtra("description", description);
        intent.putExtra("config", config);
        if (config != null) {
            // This needs to be put separately because Storable is not Serializable.
            intent.putExtra("configId", config.getId());
        }
        startActivity(intent);
    }

    /**
     * Deletes the given rule.
     * 
     * @param config
     *            The rule to be deleted.
     */
    private void doDeleteRule(final RuleConfiguration config) {
        new ActivityServerTask(this) {
            protected void executeTask(ServerConnector serverConnector) throws IOException, RequestException {
                new RuleClient(serverConnector).deleteRule(config.getId());
            }

            protected void onSuccess(Void result) {
                reloadList();
            }
        }.execute();
    }

    /**
     * List adapter for displaying each of the rule instances as a two-line item with the rule name and state.
     * 
     * @author Philipp Keck
     */
    private class RulesListAdapter extends ArrayAdapter<RuleConfiguration> {

        /**
         * Creates a new instance.
         * 
         * @param rules
         *            The rule configurations to be displayed.
         */
        public RulesListAdapter(RuleConfiguration[] rules) {
            super(RuleListActivity.this, android.R.layout.two_line_list_item, rules);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listRow = convertView;
            if (listRow == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listRow = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
            }

            TextView firstLine = (TextView) listRow.findViewById(android.R.id.text1);
            TextView secondLine = (TextView) listRow.findViewById(android.R.id.text2);

            RuleConfiguration config = getItem(position);
            firstLine.setText(config.getName());
            secondLine.setText(ResourceUtils.getString(RuleListActivity.this, "rules_" + config.getStatus().name()));

            return listRow;
        }
    }

}
