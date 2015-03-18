package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;

import de.uni_stuttgart.riot.android.messages.IM;

/**
 * This is an abstract activity for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailFragment extends ManagementFragment {

    protected long itemId;
    protected Object data;
    protected boolean enableElements;

    @Override
    protected void handleArguments(Bundle bundle) {
        this.itemId = 0;
        this.data = null;

        if (bundle != null && bundle.containsKey(BUNDLE_OBJECT_ID)) {
            this.itemId = bundle.getLong(BUNDLE_OBJECT_ID);
        }
    }

    @Override
    protected void displayData() {
        // Get values of the data object
        data = getData();
        if (data == null) {
            // Show a message that there was something wrong (FIXME output message!!)
            IM.INSTANCES.getMH().writeErrorMessage("Not data were found for displaying the detailed data!");
            return;
        }

        // Display the detailed data of the object
        displayDetailData();
    }

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract Object getData();

    /**
     * Set values to the view elements.
     */
    protected abstract void displayDetailData();
}
