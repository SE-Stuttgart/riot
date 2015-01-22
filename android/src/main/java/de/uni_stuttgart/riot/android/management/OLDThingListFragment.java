package de.uni_stuttgart.riot.android.management;

import android.view.View;
import android.widget.AdapterView;

import java.util.HashMap;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.messages.IM;

/**
 * Created by Benny on 09.01.2015.
 */
public class OLDThingListFragment extends OLDManagementListFragment {

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
        return "description";
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
        IM.INSTANCES.getMH().showQuickMessage("ThingsListFragment: At " + items.get(TAG_SUBJECT) + " (" + String.valueOf(items.get(TAG_ID)) + " )");

        //ToDo next:
        // OnClick oeffnet UserDetail
        // --> Anhand der ID werden Info abgerufen
        // --> Butteon/Stift fuer "Edit" anbieten [wenn permissions passen?]
        // .. klick auf EDIT ermoeglicht editieren der felder..
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
        items.put(TAG_DESCRIPTION, String.class);
        items.put(TAG_ONLINE_STATE, String.class);

        return items;
    }

    @Override
    protected int getTitleId() {
        return R.string.thing_list;
    }

    @Override
    protected OLDManagementFragment getFragment() {
        return this;
    }

    @Override
    protected String getUrl() {
        StringBuilder str = new StringBuilder();
        str.append("{\"things\":[");
        int max = 100;
        for (int i = 1; i < max; i++) {
            str.append("{\"id\":");
            str.append(i);
            str.append(",\"name\":\"Thing ");
            str.append(i);
            str.append("\",\"description\":\"Das ist das ");
            str.append(i);
            str.append(". Thing\",\"onlineState\":\"");

            if (i % 10 == 0) {
                str.append("away");
            } else if (i % 12 == 0) {
                str.append("busy");
            } else if (i % 24 == 0) {
                str.append("offline");
            } else if (i % 9 == 0) {
                str.append("invisible");
            } else {
                str.append("online");
            }

            str.append("\"}");
            if (i < max - 1) {
                str.append(",");
            }
        }
        str.append("]}");
        return str.toString();

//        return "http://api.learn2crack.com/android/jsonos/";
    }
}
