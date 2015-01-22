package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.HashMap;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 09.01.2015.
 */
public class UserListFragment extends ManagementListFragment {

    @Override
    protected String setTagId() {
        return "id";
    }

    @Override
    protected String setTagSubject() {
        return "name";
    }

    @Override
    protected String setTagDescription() {
        return null;
    }

    @Override
    protected String setTagPicture() {
        return null;
    }

    @Override
    protected String setTagPictureId() {
        return null;
    }

    @Override
    protected String setTagOnlineState() {
        return "onlineState";
    }

    @Override
    protected void doOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> items = (HashMap<String, Object>) parent.getItemAtPosition(position);

        // Save information for the calling fragment
        Bundle args = new Bundle();
        args.putInt(UserDetailFragment.BUNDLE_OBJECT_ID, (Integer) items.get(TAG_ID));
        callOtherFragment(new UserDetailFragment(), args);
    }

    @Override
    protected String setTagArray() {
        return null;
    }

    @Override
    protected HashMap<String, Object> setManagementItems() {
        HashMap<String, Object> items = new HashMap<String, Object>();

        // Add single elements to the items list
        items.put(TAG_ID, Integer.class);
        items.put(TAG_SUBJECT, String.class);
        items.put(TAG_ONLINE_STATE, String.class);

        return items;
    }

    @Override
    protected int getTitleId() {
        return R.string.user_list;
    }

    @Override
    protected ManagementFragment getFragment() {
        return this;
    }

    @Override
    protected String getUrl() {
        return "{\"users\":[{\"id\":1,\"name\":\"Benny\",\"roles\":[{\"rId\":\"1\",\"rName\":\"Admin\"},{\"rId\":\"2\",\"rName\":\"User\"},{\"rId\":\"3\",\"rName\":\"Guest\"}],\"permissions\":[{\"pId\":\"1\",\"pName\":\"Read\"},{\"pId\":\"2\",\"pName\":\"Write\"},{\"pId\":\"3\",\"pName\":\"Delete\"}],\"onlineState\":\"online\"},{\"id\":2,\"name\":\"Benutzer\",\"roles\":[{\"rId\":\"2\",\"rName\":\"User\"},{\"rId\":\"3\",\"rName\":\"Guest\"}],\"permissions\":[{\"pId\":\"1\",\"pName\":\"Read\"},{\"pId\":\"2\",\"pName\":\"Write\"}],\"onlineState\":\"offline\"},{\"id\":3,\"name\":\"Admin\",\"roles\":[{\"rId\":\"1\",\"rName\":\"Admin\"}],\"permissions\":[{\"pId\":\"2\",\"pName\":\"Write\"},{\"pId\":\"3\",\"pName\":\"Delete\"}],\"onlineState\":\"away\"}]}";
    }
}
