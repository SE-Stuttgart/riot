package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ManagementThingDetailFragment extends ManagementDetailFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_thing_edit;
    }

    @Override
    protected int getTitleId() {
        return R.string.thing_detail;
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyThing);
    }

    @Override
    protected void displayDetailData() {
        // Save the default values
        Long id;
        Long defaultId = getDefaultId();
        String thingName;
        String defaultThingName = getDefaultThingName();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(data)) {
            DummyThing item = (DummyThing) data;

            id = item.getId();
            thingName = item.getThingName();

            // Save the id
            if (id != null && id != 0) {
                defaultId = id;
            }

            // Save the device name
            if (thingName != null && !thingName.isEmpty()) {
                defaultThingName = thingName;
            }
        }

        // Set the id value
        if (defaultId != null && defaultId != 0) {
            ((EditText) view.findViewById(R.id.thingIdEdit)).setText(defaultId.toString());
            view.findViewById(R.id.thingIdEdit).setEnabled(this.enableElements);
        }

        // Set the thing name value
        if (defaultThingName != null && !defaultThingName.isEmpty()) {
            ((EditText) view.findViewById(R.id.thingNameEdit)).setText(defaultThingName);
            view.findViewById(R.id.thingNameEdit).setEnabled(this.enableElements);
        }
    }

    @Override
    protected Storable getData() {
        return getThing(this.itemId);
    }

    @Override
    protected void setOnAbortClick(View view) {
        if (this.enableElements) {
            Bundle args = new Bundle();
            args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementThingDetailFragment(), args);
        } else {
            callOtherFragment(new ManagementThingListFragment());
        }
    }

    @Override
    protected void setOnEditClick(View view) {
        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);

        if (this.enableElements) {
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementThingDetailFragment(), args);
        } else {
            // ToDo save changed object
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, true);
            callOtherFragment(new ManagementThingDetailFragment(), args);
        }
    }

    @Override
    protected int getAbortText() {
        return this.enableElements ? R.string.user_abort : R.string.user_back;
    }

    @Override
    protected int getEditText() {
        return this.enableElements ? R.string.user_save : R.string.user_edit;
    }

    /**
     * Returns the default id of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected Long getDefaultId() {
        return null;
    }

    /**
     * Returns the default username of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected String getDefaultThingName() {
        return null;
    }
}
