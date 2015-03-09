package de.uni_stuttgart.riot.android.serverconfiguration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.uni_stuttgart.riot.android.R;

/**
 * Dialog to edit a server configuration.
 */
public class EditConfigurationDialogFragment extends DialogFragment {

    /**
     * The listener interface for receiving server configuration change events.
     *
     * @see EditConfigurationDialogEvent
     */
    public interface EditConfigurationDialogListener {

        /**
         * called event when user choose to change the value by pressing "set".
         *
         * @param key
         *            the key name
         * @param oldValue
         *            the old value
         * @param newValue
         *            the new value
         */
        void onDialogSetClick(String key, String oldValue, String newValue);
    }

    // Use this instance of the interface to deliver set event
    private EditConfigurationDialogListener listener;

    private String keyName;
    private String title;
    private String value;
    private View contentView;

    /**
     * Instantiates a new dialog for editing a server configuration .
     *
     * @param title
     *            the title of the dialog
     * @param value
     *            the value
     * @param keyName
     *            the configuration key name
     */
    public EditConfigurationDialogFragment(String title, String value, String keyName) {
        this.title = title;
        this.value = value;
        this.keyName = keyName;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (EditConfigurationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement EditConfigurationDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // builder for alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // sets content area
        contentView = inflater.inflate(R.layout.server_setting_item_edit, null);
        EditText valueField = (EditText) contentView.findViewById(R.id.serverSettingItemEdit_value);
        valueField.setText(this.value);
        valueField.selectAll();

        builder.setView(contentView);

        // Sets the title and action buttons
        builder.setTitle(title).setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setValue();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                EditConfigurationDialogFragment.this.getDialog().cancel();
            }
        });

        // sets icon
        builder.setIcon(R.drawable.settings);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setValue() {
        String newValue = ((EditText) contentView.findViewById(R.id.serverSettingItemEdit_value)).getText().toString();
        if (!value.equals(newValue)) {
            listener.onDialogSetClick(keyName, value, newValue);
        }
    }
}
