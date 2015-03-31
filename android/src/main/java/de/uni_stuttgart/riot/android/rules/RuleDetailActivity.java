package de.uni_stuttgart.riot.android.rules;

import java.io.IOException;
import java.util.Collection;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.communication.ActivityServerTask;
import de.uni_stuttgart.riot.android.messages.IM;
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
        View control = produceControl(parameter);
        container.addView(control);

        return container;

    }

    /**
     * Creates a control for the view parameter. See {@link #produceView(ParameterDescription)}.
     * 
     * @param parameter
     *            The rule parameter.
     * @return The control for the given parameter.
     */
    protected View produceControl(ParameterDescription parameter) { // NOCS
        Object value = isAddNew ? null : config.get(parameter.getName());
        UIHint uiHint = parameter.getUiHint();
        final String parameterName = parameter.getName();
        final Class<?> valueType = parameter.getValueType();

        if (uiHint == null) {
            // Without a UIHint, we don't display any control, but only the static value.
            TextView staticValue = new TextView(this);
            staticValue.setText(value == null ? "null" : value.toString());
            return staticValue;
        }

        // Prepare the item
        if (uiHint instanceof UIHint.EditText) {
            EditText editText = new EditText(this);
            if (value != null) {
                editText.setText(value.toString());
            }
            editText.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                    config.set(parameterName, s.toString());
                }
            });
            return editText;

        } else if (uiHint instanceof UIHint.EditNumber) {
            if (value == null) {
                value = 0;
            }
            EditText editNumber = new EditText(this);
            editNumber.setText(value.toString());
            editNumber.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            editNumber.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    if (str == null || str.isEmpty()) {
                        config.set(parameterName, null);
                    } else {
                        config.set(parameterName, stringToValue(str, valueType));
                    }
                }
            });
            return editNumber;

        } else if (uiHint instanceof UIHint.ToggleButton) {
            ToggleButton toggleButton = new ToggleButton(this);
            toggleButton.setText(parameter.getName());
            if (Boolean.TRUE.equals(value)) {
                toggleButton.setChecked(true);
            }
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    config.set(parameterName, isChecked);
                }
            });
            return toggleButton;

        } else if (uiHint instanceof UIHint.IntegralSlider) {
            // TODO Remove this temporary code
            SeekBar slider = new SeekBar(this);
            final UIHint.IntegralSlider finalHint = (UIHint.IntegralSlider) uiHint;
            slider.setMax((int) (finalHint.max - finalHint.min));
            if (value != null) {
                slider.setProgress(((Number) value).intValue() - (int) finalHint.min);
            }
            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String strValue = Long.toString(progress + finalHint.min);
                    config.set(parameterName, stringToValue(strValue, valueType));
                }
            });
            return slider;

        } else if (uiHint instanceof UIHint.FractionalSlider || uiHint instanceof UIHint.IntegralSlider || uiHint instanceof UIHint.PercentageSlider) {
            // Benedikt has lots of code for these types in his branch.
            // The code should be integrated with this one.
            // Possible solution: Refactor the ThingProperties of Benedikt to ViewAdapters or something like that.
            // A ViewAdapter takes an initialvalue and a valuetype and fires change events for the respective values to a listener.
            // An additional Binding class could connect ThingProperties with the ViewAdapters and this class here could use the
            // ViewAdapters directly.
            throw new UnsupportedOperationException("This type is not yet supported");

        } else if (uiHint instanceof UIHint.EnumDropDown) {
            // See above.
            throw new UnsupportedOperationException("This type is not yet supported");

        } else if (uiHint instanceof UIHint.ThingDropDown) {
            final Spinner spinner = new Spinner(this);
            spinner.setEnabled(false);
            final long initialValue = (value == null) ? 0 : (Long) value;
            final UIHint.ThingDropDown finalHint = (UIHint.ThingDropDown) uiHint;

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

        } else {
            throw new UnsupportedOperationException("Unknown type in preparePropertyItems()!");
        }
    }

    /**
     * Calls the <tt>valueOf</tt> method of the given class <tt>valueType</tt> to convert the given <tt>value</tt>.
     * 
     * @param <T>
     *            The value type.
     * @param value
     *            The value.
     * @param valueType
     *            The value type.
     * @return The converted value.
     */
    private static <T> T stringToValue(String value, Class<T> valueType) {
        if (value == null) {
            return null;
        } else if (valueType == String.class) {
            return valueType.cast(value);
        }
        try {
            @SuppressWarnings("unchecked")
            T result = (T) valueType.getMethod("valueOf", String.class).invoke(null, value);
            return result;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The type " + valueType + " is not supported!");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception when calling " + valueType + "#valueOf(" + value + ")");
        }
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

            TextView text = new TextView(RuleDetailActivity.this);
            text.setText(thingInfo.getId() + " " + metainfo.getName());
            return text;
        }

    }

}
