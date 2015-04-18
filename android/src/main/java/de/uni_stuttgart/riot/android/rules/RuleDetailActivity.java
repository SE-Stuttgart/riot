package de.uni_stuttgart.riot.android.rules;

import java.io.IOException;
import java.util.Collection;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.uni_stuttgart.riot.android.Callback;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.communication.ActivityServerTask;
import de.uni_stuttgart.riot.android.management.LetterTileProvider;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.ui.PropertyView;
import de.uni_stuttgart.riot.android.ui.UIProducer;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.client.RuleClient;
import de.uni_stuttgart.riot.thing.ParameterDescription;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingMetainfo;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This activity displays the parameters of a rule and also allows for creating new rules.
 * 
 * @author Philipp Keck
 */
public class RuleDetailActivity extends Activity {

    boolean isAddNew;
    RuleConfiguration config;
    RuleDescription description;

    EditText ruleName;
    ToggleButton ruleActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = (RuleConfiguration) getIntent().getSerializableExtra("config");
        description = (RuleDescription) getIntent().getSerializableExtra("description");
        isAddNew = (config == null);
        if (isAddNew) {
            config = new RuleConfiguration(description.getType());
            config.setStatus(RuleStatus.ACTIVE);
            config.setName("");
        } else {
            config.setId(getIntent().getLongExtra("configId", 0));
        }

        // Set the title and icon of the action bar.
        ActionBar actionBar = getActionBar();
        if (isAddNew) {
            actionBar.setTitle(getString(R.string.rules_btnAddRule));
        } else {
            actionBar.setTitle(config.getName());
        }
        actionBar.setIcon(R.drawable.rules);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create view container.
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        setContentView(container);

        // Create common inputs.
        TextView lblName = new TextView(this);
        lblName.setText(R.string.rules_name);
        container.addView(lblName);
        ruleName = new EditText(this);
        ruleName.setText(config.getName());
        ruleName.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                config.setName(s.toString());
            }
        });
        container.addView(ruleName);

        ruleActive = new ToggleButton(this);
        ruleActive.setText(config.getStatus().name());
        ruleActive.setChecked(config.getStatus() == RuleStatus.ACTIVE);
        ruleActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setStatus(isChecked ? RuleStatus.ACTIVE : RuleStatus.DEACTIVATED);
                ruleActive.setText(config.getStatus().name());
            }
        });

        // Create generic parameter inputs.
        for (ParameterDescription parameter : description.getParameters()) {
            container.addView(produceView(parameter));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.ok).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            doCompleteRule();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Checks that all parameters are there and then creates the rule or updates it.
     */
    private void doCompleteRule() {
        // Validate name.
        if (config.getName() == null || config.getName().isEmpty()) {
            IM.INSTANCES.getMH().showMessage(getString(R.string.rules_errorMissingName));
            return;
        }

        // Validate parameter values.
        for (ParameterDescription parameter : description.getParameters()) {
            Object value = config.get(parameter.getName());
            if (value == null) {
                IM.INSTANCES.getMH().showMessage(getString(R.string.rules_errorMissingParameter) + parameter.getName());
                return;
            } else if (!parameter.getInternalValueType().isInstance(value)) {
                throw new RuntimeException("Mismatching parameter type " + value.getClass() + ", expected " + parameter.getInternalValueType() + " for parameter " + parameter.getName());
            }
        }

        // All good, now create or update it.
        new ActivityServerTask(this) {
            protected void executeTask(ServerConnector serverConnector) throws IOException, RequestException {
                if (isAddNew) {
                    new RuleClient(serverConnector).addNewRule(config);
                } else {
                    new RuleClient(serverConnector).updateRuleConfiguration(config);
                }
            }

            protected void onSuccess(Void result) {
                finish();
            }
        }.execute();

    }

    /**
     * Creates the view for a rule parameter. The rule consists of a label and a control. If {@link #config} is set, the control will be
     * initialized with the current parameter value. Otherwise, it will be set to some default value. Changes that the user makes to the
     * control will be written into {@link #config} directly.
     * 
     * @param parameter
     *            The rule parameter.
     * @return The view for the parameter.
     */
    protected View produceView(ParameterDescription parameter) {

        // Create containing layout.
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        // Create label.
        TextView label = new TextView(this);
        label.setText(parameter.getName());
        container.addView(label);

        // Create control.
        Object initialValue = isAddNew ? null : config.get(parameter.getName());
        View control = produceControl(parameter.getUiHint(), parameter.getName(), parameter.getValueType(), initialValue);
        container.addView(control);

        return container;

    }

    /**
     * Creates a control for the rule parameter. See {@link #produceView(ParameterDescription)}.
     * 
     * @param <V>
     *            The type of the parameter's values.
     * @param hint
     *            The UI hint (may be <tt>null</tt>).
     * @param parameterName
     *            The name of rule parameter.
     * @param valueType
     *            The type of the parameter's values.
     * @param initialValue
     *            The initial value to be displayed (may be <tt>null</tt>).
     * @return The control for the given parameter.
     */
    protected <V> View produceControl(UIHint hint, final String parameterName, Class<V> valueType, final Object initialValue) {
        if (hint == null) {
            // Without a UIHint, we don't display any control, but only the static value.
            TextView staticValue = new TextView(this);
            staticValue.setText(initialValue == null ? "null" : initialValue.toString());
            return staticValue;
        }

        // Handle ThingDropDowns separately
        if (hint instanceof UIHint.ThingDropDown) {
            final Spinner spinner = new Spinner(this);
            spinner.setEnabled(false);
            final UIHint.ThingDropDown finalHint = (UIHint.ThingDropDown) hint;

            new ActivityServerConnection<Collection<ThingInformation>>(this) {
                protected Collection<ThingInformation> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                    return new ThingClient(serverConnector).findThings(finalHint.targetType.getName(), finalHint.getThingPermissions());
                }

                protected void onSuccess(Collection<ThingInformation> result) {
                    final ThingInformation[] thingInfos = result.toArray(new ThingInformation[result.size()]);
                    spinner.setAdapter(new ChooseThingListAdapter(thingInfos));
                    for (int i = 0; i < thingInfos.length; i++) {
                        if (thingInfos[i].getId().equals(initialValue)) {
                            spinner.setSelection(i);
                            break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            config.set(parameterName, thingInfos[position].getId());
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            config.set(parameterName, null);
                        }
                    });
                    spinner.setEnabled(true);
                }
            }.execute();
            return spinner;
        }

        // Prepare the item
        PropertyView<V> view = UIProducer.producePropertyView(this, hint, valueType, parameterName);
        view.setValue(valueType.cast(initialValue));
        view.setListener(new Callback<V>() {
            public void callback(V value) {
                config.set(parameterName, value);
            }
        });
        return view.toView();

    }

    /**
     * A list adapter to display a list of possible things in the spinner. The user can choose one thing as the value for a rule parameter.
     */
    private class ChooseThingListAdapter extends BaseAdapter {

        private final ThingInformation[] thingInfos;

        /**
         * Creates a new instance.
         * 
         * @param thingInfos
         *            The thing infos to be displayed. Only the meta-info is used.
         */
        public ChooseThingListAdapter(ThingInformation[] thingInfos) {
            this.thingInfos = thingInfos;
        }

        @Override
        public int getCount() {
            return thingInfos.length;
        }

        @Override
        public Object getItem(int position) {
            return thingInfos[position];
        }

        @Override
        public long getItemId(int position) {
            return thingInfos[position].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ThingInformation thingInfo = thingInfos[position];
            ThingMetainfo metainfo = thingInfo.getMetainfo();

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.managment_list_item, parent, false);
            }

            ((TextView) view.findViewById(R.id.list_item_management_subject)).setText(thingInfo.getId() + " " + metainfo.getName());
            ((TextView) view.findViewById(R.id.list_item_management_description)).setText(getString(R.string.default_thing_description) + " (" + thingInfo.getId() + ")");
            ((ImageView) view.findViewById(R.id.list_item_management_picture)).setImageDrawable(new BitmapDrawable(getResources(), new LetterTileProvider(RuleDetailActivity.this).getLetterTile(metainfo.getName(), String.valueOf(thingInfo.getId()))));
            return view;
        }

    }

}
