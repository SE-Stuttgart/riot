package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * This is an abstract fragment for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailFragment extends ManagementFragment {

    protected static final String BUNDLE_ENABLE_ELEMENTS = "bundle_enable_elements";

    protected long itemId;
    protected boolean enableElements;
    protected Storable data;

    @Override
    protected void handleArguments(Bundle args) {
        if (args != null) {
            // Save the value of the parameter, otherwise set a default id
            this.itemId = args.containsKey(BUNDLE_OBJECT_ID) ? args.getLong(BUNDLE_OBJECT_ID) : 0;

            // Save the value of the parameter, otherwise disable all elements
            this.enableElements = args.containsKey(BUNDLE_ENABLE_ELEMENTS) && args.getBoolean(BUNDLE_ENABLE_ELEMENTS);
        }
    }

    @Override
    protected void displayData() {
        // Set values and on click event off the buttons
        Button buttonAbort = (Button) view.findViewById(R.id.detailAbortButton);
        buttonAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnAbortClick(view);
            }
        });
        buttonAbort.setText(getAbortText());

        Button buttonEdit = (Button) view.findViewById(R.id.detailEditButton);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnEditClick(view);
            }
        });
        buttonEdit.setText(getEditText());


        // Set values of the data objects
        data = getData();
        if (data == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        displayDetailData();
    }

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract Storable getData();

    /**
     * Set values to the view elements.
     */
    protected abstract void displayDetailData();


    /**
     * Define the action that happens on clicking the abort button.
     *
     * @param view is not really used?
     */
    protected abstract void setOnAbortClick(View view);

    /**
     * Define the action that happens on clicking the save button.
     *
     * @param view is not really used?
     */
    protected abstract void setOnEditClick(View view);

    /**
     * Returns the text that is displayed on the abort button.
     *
     * @return the id of the string resource
     */
    protected abstract int getAbortText();

    /**
     * Returns the text that is displayed on the save button.
     *
     * @return the id of the string resource
     */
    protected abstract int getEditText();
}
