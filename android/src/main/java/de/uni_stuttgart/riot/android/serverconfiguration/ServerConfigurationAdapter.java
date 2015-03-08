package de.uni_stuttgart.riot.android.serverconfiguration;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;

/**
 * The customized Adapter for showing configuration items on an ListView.
 */
public class ServerConfigurationAdapter extends ArrayAdapter<ConfigurationEntry> {

    /** The context. */
    private Context context;

    /** The resource id. */
    private int resourceId;

    /** The data. */
    private List<ConfigurationEntry> data;

    /**
     * Instantiates a new server configuration adapter.
     *
     * @param context
     *            the context
     * @param resourceId
     *            the resource id
     * @param data
     *            the data
     */
    public ServerConfigurationAdapter(Context context, int resourceId, List<ConfigurationEntry> data) {
        super(context, resourceId, data);

        this.context = context;
        this.resourceId = resourceId;
        this.data = data;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get current row view
        View listRow = convertView;
        if (listRow == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listRow = inflater.inflate(resourceId, parent, false);
        }

        ConfigurationEntry element = data.get(position);
        String configKey = element.getConfigKey();
        String configValue = element.getConfigValue();
        // Set the subject
        if (configKey != null && !configKey.isEmpty()) {
            ((TextView) listRow.findViewById(R.id.serverSettingItem_subject)).setText(getSubjectResourceId(configKey));

            // Set the description
            ((TextView) listRow.findViewById(R.id.serverSettingItem_description)).setText(getDescriptionResourceId(configKey));

            // Set the value
            ((TextView) listRow.findViewById(R.id.serverSettingItem_value)).setText(configValue);
        }
        return listRow;
    }

    /**
     * Returns the resource id of corresponding subject text of given configuration key.
     *
     * @param configKey
     *            the config key
     * @return the subject resource id
     */
    private int getSubjectResourceId(String configKey) {
        switch (ConfigurationKey.valueOf(configKey)) {
        case um_hashIterations:
            return R.string.configuration_um_hashIterations;
        case um_authTokenValidTime:
            return R.string.configuration_um_authTokenValidTime;
        case um_maxLoginRetries:
            return R.string.configuration_um_maxLoginRetries;
        case um_pwValidator_allowedSpecialChars:
            return R.string.configuration_um_pwValidator_allowedSpecialChars;
        case um_pwValidator_lowerCaseCharsCount:
            return R.string.configuration_um_pwValidator_lowerCaseCharsCount;
        case um_pwValidator_maxLength:
            return R.string.configuration_um_pwValidator_maxLength;
        case um_pwValidator_minLength:
            return R.string.configuration_um_pwValidator_minLength;
        case um_pwValidator_numberCount:
            return R.string.configuration_um_pwValidator_numberCount;
        case um_pwValidator_specialCharsCount:
            return R.string.configuration_um_pwValidator_specialCharsCount;
        case um_pwValidator_upperCaseCharCount:
            return R.string.configuration_um_pwValidator_upperCaseCharCount;
        default:
            return R.string.unknownName;
        }
    }

    /**
     * Returns the resource id of corresponding description text of given configuration key.
     *
     * @param configKey
     *            the config key
     * @return the description resource id
     */
    private int getDescriptionResourceId(String configKey) {
        switch (ConfigurationKey.valueOf(configKey)) {
        case um_hashIterations:
            return R.string.configuration_um_hashIterations_description;
        case um_authTokenValidTime:
            return R.string.configuration_um_authTokenValidTime_description;
        case um_maxLoginRetries:
            return R.string.configuration_um_maxLoginRetries_description;
        case um_pwValidator_allowedSpecialChars:
            return R.string.configuration_um_pwValidator_allowedSpecialChars_description;
        case um_pwValidator_lowerCaseCharsCount:
            return R.string.configuration_um_pwValidator_lowerCaseCharsCount_description;
        case um_pwValidator_maxLength:
            return R.string.configuration_um_pwValidator_maxLength_description;
        case um_pwValidator_minLength:
            return R.string.configuration_um_pwValidator_minLength_description;
        case um_pwValidator_numberCount:
            return R.string.configuration_um_pwValidator_numberCount_description;
        case um_pwValidator_specialCharsCount:
            return R.string.configuration_um_pwValidator_specialCharsCount_description;
        case um_pwValidator_upperCaseCharCount:
            return R.string.configuration_um_pwValidator_upperCaseCharCount_description;
        default:
            return R.string.unknownName;
        }
    }
}
