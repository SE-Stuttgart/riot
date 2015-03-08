package de.uni_stuttgart.riot.android.management;

import java.util.List;

<<<<<<< HEAD
<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
>>>>>>> RIOT-87:Android:Get things from the server
=======
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data
import de.uni_stuttgart.riot.commons.model.OnlineState;

/**
 * Fragment that displays all users in a list.
 *
 * @author Benny
 */
public class ManagementUserListFragment extends ManagementListFragment {
    @Override
    protected List<Object> getData() {
        return null;
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
    protected long getId(Object item) {
        return 0;
    }

    @Override
    protected String getDefaultSubject() {
        return null;
    }

    @Override
    protected String getSubject(Object item) {
        return null;
    }

    @Override
    protected String getDefaultDescription() {
        return null;
    }

    @Override
    protected String getDescription(Object item) {
        return null;
    }

    @Override
    protected String getDefaultImageUri() {
        return null;
    }

    @Override
    protected String getImageUri(Object item) {
        return null;
    }

    @Override
    protected int getDefaultImageId() {
        return 0;
    }

    @Override
    protected int getImageId(Object item) {
        return 0;
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return null;
    }

    @Override
    protected OnlineState getOnlineState(Object item) {
        return null;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return false;
    }

//    @Override
//    protected String getPageTitle() {
//        return getResources().getString(R.string.user_list);
//    }
//
//    @Override
//    protected boolean isInstanceOf(Object item) {
//        return (item instanceof User);
//    }
//
//    @Override
//    protected List<Object> getData() {
//        try {
//            Collection<User> users = RIOTApiClient.getInstance().getUserManagementClient().getUsers();
//            return new ArrayList<Object>(users);
//        } catch (Exception e) {
//            // FIXME output message!!
//            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: " + e.getMessage());
//        }
//
//        // Load dummy objects if the above method was not successful
//        return (List<Object>) (List<?>) getUsers();
//    }
//
//    @Override
//    protected ManagementListFragment getFragment() {
//        return this;
//    }
//
//    @Override
//    protected Fragment getOnItemClickActivity() {
//        return new ManagementUserDetailFragment();
//    }
//
//    @Override
//    protected long getId(Object item) {
//        return ((User) item).getId();
//    }
//
//    @Override
//    protected String getDefaultSubject() {
//        return "This is a User";
//    }
//
//    @Override
//    protected String getSubject(Object item) {
//        return ((User) item).getUsername();
//    }
//
//    @Override
//    protected String getDefaultDescription() {
//        return "This is the description of the user";
//    }
//
//    @Override
//    protected String getDescription(Object item) {
//        return ((User) item).getEmail();
//    }
//
//    @Override
//    protected String getDefaultImageUri() {
//        return null; // ToDo loading images asynchronous?
//    }
//
//    @Override
//    protected String getImageUri(Object item) {
//        return null;
//    }
//
//    @Override
//    protected int getDefaultImageId() {
//        return android.R.drawable.ic_menu_gallery;
//    }
//
//    @Override
//    protected int getImageId(Object item) {
//        return 0; // TODO
//    }
//
//    @Override
//    protected OnlineState getDefaultOnlineState() {
//        return OnlineState.STATUS_OFFLINE;
//    }
//
//    @Override
//    protected OnlineState getOnlineState(Object item) {
//        return null; // TODO
//    }
}
