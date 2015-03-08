package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all details of a device.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ManagementDeviceDetailFragment extends ManagementDetailFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_device_edit;
    }

    @Override
    protected int getTitleId() {
        return R.string.device_detail;
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyDevice);
    }

    @Override
    protected void displayDetailData() {
        // Save the default values
        Long id;
        Long defaultId = getDefaultId();
        String deviceName;
        String defaultDeviceName = getDefaultDeviceName();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(data)) {
            DummyDevice item = (DummyDevice) data;

            id = item.getId();
            deviceName = item.getDeviceName();

            // Save the id
            if (id != null && id != 0) {
                defaultId = id;
            }

            // Save the device name
            if (deviceName != null && !deviceName.isEmpty()) {
                defaultDeviceName = deviceName;
            }
        }

        // Set the id value
        if (defaultId != null && defaultId != 0) {
            ((EditText) view.findViewById(R.id.deviceIdEdit)).setText(defaultId.toString());
            view.findViewById(R.id.deviceIdEdit).setEnabled(this.enableElements);
        }

        // Set the device name value
        if (defaultDeviceName != null && !defaultDeviceName.isEmpty()) {
            ((EditText) view.findViewById(R.id.deviceNameEdit)).setText(defaultDeviceName);
            view.findViewById(R.id.deviceNameEdit).setEnabled(this.enableElements);
        }
    }

    @Override
    protected Storable getData() {
        return getDevice(this.itemId);
    }

    @Override
    protected void setOnAbortClick(View view) {
        if (this.enableElements) {
            Bundle args = new Bundle();
            args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementDeviceDetailFragment(), args);
        } else {
            callOtherFragment(new ManagementDeviceListFragment());
        }
    }

    @Override
    protected void setOnEditClick(View view) {
        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);

        if (this.enableElements) {
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementDeviceDetailFragment(), args);
        } else {
            // ToDo save changed object
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, true);
            callOtherFragment(new ManagementDeviceDetailFragment(), args);
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
    protected String getDefaultDeviceName() {
        return null;
    }
}
