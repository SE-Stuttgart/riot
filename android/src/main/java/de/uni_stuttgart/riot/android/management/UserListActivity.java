package de.uni_stuttgart.riot.android.management;

import java.io.IOException;
import java.util.ArrayList;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Activity that displays all users in a list.
 *
 * @author Benny
 */
public class UserListActivity extends ManagementListActivity {

    @Override
    protected void getAndDisplayListData() {
        new ActivityServerConnection<ArrayList<Object>>(getActivity()) {
            @Override
            protected ArrayList<Object> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    return new ArrayList<Object>(new UsermanagementClient(serverConnector).getUsers());
                    // ToDo I want to get an ArrayList instead of a Collection
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
                }
                return null;
            }

            @Override
            protected void onSuccess(ArrayList<Object> result) {
                // ToDo do the same for the thing views or change it on some way (to use the ManagementActivity)
                displayData(result);

                // End processing animation
                startProcessingAnimation(false);
            }
        }.execute();
    }

    @Override
    protected ManagementListActivity getActivity() {
        return this;
    }

    @Override
    protected Class getOnItemClickActivity() {
        return UserDetailActivity.class;
    }

    @Override
    protected long getId(Object item) {
        return ((User) item).getId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.user_name);
    }

    @Override
    protected String getSubject(Object item) {
        return ((User) item).getUsername();
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.user_mail);
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
    }

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
    protected OnlineState getOnlineState(final Object item) {
        if (item != null) {
            new ActivityServerConnection<OnlineState>(getActivity()) {
                @Override
                protected OnlineState executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                    try {
                        return new UsermanagementClient(serverConnector).getOnlineState(((User) item).getId());
                    } catch (Exception e) {
                        IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state: ", e);
                        // IM.INSTANCES.getMH().showQuickMessage("Problems by getting online state!");
                    }
                    return null;
                }

                @Override
                protected void onSuccess(OnlineState result) {

                }
            }.execute();
        }
        return null;
    }

    @Override
    protected String getDetailPageTitle(Object item) {
        // Get the name of the item as title
        if (isInstanceOf(item)) {
            return ((User) item).getUsername();
        }
        return null;
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
