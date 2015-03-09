package de.uni_stuttgart.riot.android.management;

import android.view.View;

import java.util.List;

<<<<<<< HEAD
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
=======
import de.enpro.android.riot.R;
>>>>>>> RIOT-87:Android:Do exchange data with server (also update things) and
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Activity that displays all users in a list.
 *
 * @author Benny
 */
public class ManagementUserListFragment extends ManagementListFragment {
    // ToDo!!

    @Override
    protected List<Object> getData() {
        return null;
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Class getOnItemClickActivity() {
        return ManagementUserDetailFragment.class;
    }

    @Override
    protected long getId(Object item) {
        return ((User) item).getId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.default_user);
    }

    @Override
    protected String getSubject(Object item) {
        return ((User) item).getUsername();
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.default_user_description);
    }

    @Override
    protected String getDescription(Object item) {
        return ((User) item).getEmail();
    }

    @Override
    protected String getDefaultImageUri() {
        return null;
    }

    @Override
    protected String getImageUri(Object item) {
        return null;
    }// ToDo loading images asynchronous?

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Object item) {
        return 0;
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected void getOnlineState(Object item, View view) {
    }

    @Override
    protected String getPageTitle() {
        return getResources().getString(R.string.user_list);
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof User);
    }
}
