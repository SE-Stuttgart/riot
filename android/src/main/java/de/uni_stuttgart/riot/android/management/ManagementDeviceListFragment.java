package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;

import java.util.List;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all devices in a list.
 *
 * @author Benny
 */
public class ManagementDeviceListFragment extends ManagementListFragment {

    @Override
    protected int getTitleId() {
        return R.string.device_list;
    }

    @Override
    protected List<Storable> getData() {
        return (List<Storable>) (List<?>) getDevices();
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Fragment getOnItemClickFragment() {
        return new ManagementDeviceDetailFragment();
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyDevice);
    }

    @Override
    protected String getDefaultSubject() {
        return "This is a Device";
    }

    @Override
    protected String getSubject(Storable item) {
        return ((DummyDevice) item).getDeviceName();
    }

    @Override
    protected String getDefaultDescription() {
        return "This is the description of the device";
    }

    @Override
    protected String getDescription(Storable item) {
//        Long id = ((DummyDevice) item).getId();
//        if (id != null && id != 0) {
//            return id.toString();
//        }
        return null;
    }

    @Override
    protected String getDefaultImageUri() {
        return null; // ToDo loading images asynchronous?
    }

    @Override
    protected String getImageUri(Storable item) {
        return null; // ((DummyDevice) item).getImageUri(); // ToDo
    }

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Storable item) {
        return 0; // ((DummyDevice) item).getImageId(); // ToDo
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(Storable item) {
        return null; // ((DummyDevice) item).getOnlineState(); // ToDo
    }
}
