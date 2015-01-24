package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;

import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Created by Benny on 24.01.2015
 */
public class ManagementUserListFragment extends ManagementListFragment {

    @Override
    protected int getTitleId() {
        return R.string.user_list;
    }

    @Override
    protected List<Storable> getData() {
        return (List<Storable>) (List<?>) getUsers();
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Fragment getOnItemClickFragment() {
        return new UserDetailFragment();
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyUser);
    }

    @Override
    protected String getDefaultSubject() {
        return "This is a User";
    }

    @Override
    protected String getSubject(Storable item) {
        return ((DummyUser) item).getUsername();
    }

    @Override
    protected String getDefaultDescription() {
        return "This is the description of the user";
    }

    @Override
    protected String getDescription(Storable item) {
        return ((DummyUser) item).getId().toString();
    }

    @Override
    protected String getDefaultImageUri() {
        return null; // ToDo loading images asynchronous?
    }

    @Override
    protected String getImageUri(Storable item) {
        return ((DummyUser) item).getImageUri();
    }

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Storable item) {
        return ((DummyUser) item).getImageId();
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(Storable item) {
        return ((DummyUser) item).getOnlineState();
    }
}
