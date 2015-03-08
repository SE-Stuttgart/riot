package de.uni_stuttgart.riot.android.management;

import java.util.List;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all devices in a list.
 *
 * @author Benny
 */
public class ManagementDeviceListFragment extends ManagementListFragment {
    @Override
<<<<<<< HEAD
    protected List<Storable> getData() {
        return (List<Storable>) (List<?>) getDevices();
=======
    protected List<Object> getData() {
        return null;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected ManagementListFragment getFragment() {
        return null;
    }

    @Override
    protected Class getOnItemClickActivity() {
        return null;
    }

    @Override
<<<<<<< HEAD
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyDevice);
=======
    protected long getId(Object item) {
        return 0;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected String getDefaultSubject() {
        return null;
    }

    @Override
<<<<<<< HEAD
    protected String getSubject(Storable item) {
        return ((DummyDevice) item).getDeviceName();
=======
    protected String getSubject(Object item) {
        return null;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected String getDefaultDescription() {
        return null;
    }

    @Override
<<<<<<< HEAD
    protected String getDescription(Storable item) {
//        Long id = ((DummyDevice) item).getId();
//        if (id != null && id != 0) {
//            return id.toString();
//        }
=======
    protected String getDescription(Object item) {
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
        return null;
    }

    @Override
    protected String getDefaultImageUri() {
        return null;
    }

    @Override
<<<<<<< HEAD
    protected String getImageUri(Storable item) {
        return null; // ((DummyDevice) item).getImageUri(); // ToDo
=======
    protected String getImageUri(Object item) {
        return null;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected int getDefaultImageId() {
        return 0;
    }

    @Override
<<<<<<< HEAD
    protected int getImageId(Storable item) {
        return 0; // ((DummyDevice) item).getImageId(); // ToDo
=======
    protected int getImageId(Object item) {
        return 0;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return null;
    }

    @Override
<<<<<<< HEAD
    protected OnlineState getOnlineState(Storable item) {
        return ((DummyDevice) item).getOnlineState(); // ToDo
=======
    protected OnlineState getOnlineState(Object item) {
        return null;
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return false;
    }
//
//    @Override
//    protected String getPageTitle() {
//        return getResources().getString(R.string.device_list);
//    }
//
//    @Override
//    protected List<Object> getData() {
//        return (List<Object>) (List<?>) getDevices();
//    }
//
//    @Override
//    protected ManagementListFragment getFragment() {
//        return this;
//    }
//
//    @Override
//    protected Fragment getOnItemClickActivity() {
//        return new ManagementDeviceDetailFragment();
//    }
//
//    @Override
//    protected long getId(Object item) {
//        return 0;
//    }
//
//    @Override
//    protected boolean isInstanceOf(Object item) {
//        return (item instanceof DummyDevice);
//    }
//
//    @Override
//    protected String getDefaultSubject() {
//        return "This is a Device";
//    }
//
//    @Override
//    protected String getSubject(Object item) {
//        return ((DummyDevice) item).getDeviceName();
//    }
//
//    @Override
//    protected String getDefaultDescription() {
//        return "This is the description of the device";
//    }
//
//    @Override
//    protected String getDescription(Object item) {
////        Long id = ((DummyDevice) item).getId();
////        if (id != null && id != 0) {
////            return id.toString();
////        }
//        return null;
//    }
//
//    @Override
//    protected String getDefaultImageUri() {
//        return null; // ToDo loading images asynchronous?
//    }
//
//    @Override
//    protected String getImageUri(Object item) {
//        return null; // ((DummyDevice) item).getImageUri(); // ToDo
//    }
//
//    @Override
//    protected int getDefaultImageId() {
//        return android.R.drawable.ic_menu_gallery;
//    }
//
//    @Override
//    protected int getImageId(Object item) {
//        return 0; // ((DummyDevice) item).getImageId(); // ToDo
//    }
//
//    @Override
//    protected OnlineState getDefaultOnlineState() {
//        return OnlineState.STATUS_OFFLINE;
//    }
//
//    @Override
//    protected OnlineState getOnlineState(Object item) {
//        return ((DummyDevice) item).getOnlineState(); // ToDo
//    }
}
