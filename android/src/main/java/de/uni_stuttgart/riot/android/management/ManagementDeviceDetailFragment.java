package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.widget.EditText;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
=======
import de.enpro.android.riot.R;
>>>>>>> RIOT-87:Android:Get things from the server

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
    protected String getTitle() {
        return getResources().getString(R.string.device_detail);
    }

    @Override
    protected boolean isInstanceOf(Object item) {
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
            ((EditText) view.findViewById(R.id.device_id_edit)).setText(defaultId.toString());
            view.findViewById(R.id.device_id_edit).setEnabled(this.enableElements);
        }

        // Set the device name value
        if (defaultDeviceName != null && !defaultDeviceName.isEmpty()) {
            ((EditText) view.findViewById(R.id.device_name_edit)).setText(defaultDeviceName);
            view.findViewById(R.id.device_name_edit).setEnabled(this.enableElements);
        }
    }

    @Override
    protected void setOnBackClick() {
        callOtherFragment(new ManagementDeviceListFragment());
    }

    @Override
    protected void saveChanges() {

    }

    @Override
    protected void callThisFragment(boolean edit) {
        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
        args.putBoolean(BUNDLE_ENABLE_ELEMENTS, edit);
        callOtherFragment(new ManagementDeviceDetailFragment(), args);
    }

    @Override
    protected Object getData() {
        return getDevice(this.itemId);
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
