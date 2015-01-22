package de.uni_stuttgart.riot.android.management;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 09.01.2015.
 */
public abstract class ManagementListFragment extends ManagementFragment {

    // Attributes for identify data
    protected String TAG_ID;
    protected String TAG_SUBJECT;
    protected String TAG_DESCRIPTION;
    protected String TAG_PICTURE;
    protected String TAG_PICTURE_ID;
    protected String TAG_ONLINE_STATE;

    protected ManagementListFragment() {
        // Get values for the identifier
        TAG_ID = setTagId();
        TAG_SUBJECT = setTagSubject();
        TAG_DESCRIPTION = setTagDescription();
        TAG_PICTURE = setTagPicture();
        TAG_PICTURE_ID = setTagPictureId();
        TAG_ONLINE_STATE = setTagOnlineState();
    }

    protected abstract String setTagId();

    protected abstract String setTagSubject();

    protected abstract String setTagDescription();

    protected abstract String setTagPicture();

    protected abstract String setTagPictureId();

    protected abstract String setTagOnlineState();

    abstract protected void doOnItemClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_management_list;
    }

    @Override
    protected void displayData(List<HashMap<String, Object>> itemsList) {
        // Install list adapter and on item click listener
        ManagementListAdapter managementListAdapter = new ManagementListAdapter(this, getActivity(), R.layout.list_item_managment_list, itemsList);
        ListView listView = (ListView) getActivity().findViewById(R.id.management_listView);
        listView.setAdapter(managementListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnItemClick(parent, view, position, id);
            }
        });
        // If you want to change items afterwards (when list adapter is already installed) use:
        // managementListAdapter.notifyDataSetChanged();
    }

    public void doGetView(View view, HashMap<String, Object> items) {
        // Check the item names and set the value on the correct display object
        if (items.containsKey(TAG_SUBJECT) && items.get(TAG_SUBJECT).getClass().equals(String.class)) {
            ((TextView) view.findViewById(R.id.listItem_management_subject)).setText((String) items.get(TAG_SUBJECT));
        }
        if (items.containsKey(TAG_DESCRIPTION) && items.get(TAG_DESCRIPTION).getClass().equals(String.class)) {
            ((TextView) view.findViewById(R.id.listItem_management_description)).setText((String) items.get(TAG_DESCRIPTION));
        }
        if (items.containsKey(TAG_PICTURE) && items.get(TAG_PICTURE).getClass().equals(String.class)) {
            // ToDo -> Wie setzt man ein Bild anhand des Pfads?
            // ((ImageView) view.findViewById(R.id.listItem_management_picture)).setImageURI((String) items.get(TAG_PICTURE));
        }
        if (items.containsKey(TAG_PICTURE_ID) && items.get(TAG_PICTURE_ID).getClass().equals(Integer.class)) {
            ((ImageView) view.findViewById(R.id.listItem_management_picture)).setImageResource((Integer) items.get(TAG_PICTURE_ID));
        }
        if (items.containsKey(TAG_ONLINE_STATE) && items.get(TAG_ONLINE_STATE).getClass().equals(String.class)) {
            ((ImageView) view.findViewById(R.id.listItem_management_onlineState)).setImageResource((OnlineState.getEnumByValue((String) items.get(TAG_ONLINE_STATE)).getR()));
        }
        // ToDo Default values setzten fuer die listenelemente..
    }
}
