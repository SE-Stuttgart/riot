package de.uni_stuttgart.riot.android.management;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 10.01.2015.
 */
public abstract class ManagementFragment extends Fragment {

    // Attributes
    protected View view;
    private String TAG_ARRAY;
    private HashMap<String, Object> managementItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(getLayoutResource(), container, false);

        // Set the tag names for the json object
        TAG_ARRAY = setTagArray();

        // Set the post tags to get values of the json object
        managementItems = setManagementItems();

        // Set the title of the frame
        getActivity().setTitle(getTitleId());

        // Get data from the server
        JSONAsyncTask jsonAsyncTask = new JSONAsyncTask(getFragment(), getUrl());
        jsonAsyncTask.execute();
        return this.view;
    }

    protected abstract int getLayoutResource();

    protected abstract String setTagArray();

    protected abstract HashMap<String, Object> setManagementItems();

    protected abstract int getTitleId();

    protected abstract ManagementFragment getFragment();

    protected abstract String getUrl();

    /**
     * Display an all data
     *
     * @param itemsList is the list of all items
     */
    protected abstract void displayData(List<HashMap<String, Object>> itemsList);

    /**
     * Get the json array from the json object
     *
     * @param jsonObject the data object that comes from the server
     */
    public void doOnPostExecute(JSONObject jsonObject) {
        String tag = "";
        if (TAG_ARRAY != null) {
            // If array tag is not null use the saved value as tag
            tag = TAG_ARRAY;
        } else {
            // Else use the first key of the json object as tag
            Iterator keys = jsonObject.keys();
            if (keys.hasNext()) {
                tag = (String) keys.next();
            }
        }
        try {
            // Get data array from data object
            doOnPostExecute(jsonObject.getJSONArray(tag));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Defines what to do when the data arrived
     * It is possible to @ Override this function in the subclasses
     *
     * @param jsonArray the data object that comes from the server
     */
    public void doOnPostExecute(JSONArray jsonArray) {
        if (jsonArray == null) {
            // ToDo: Was dann, eine Fehlermeldung??
        } else {
            // Next step is to display the prepared data
            displayData(getValues(managementItems, jsonArray));
        }
    }

    /**
     * Fill the given list of managementItems with the matching values
     *
     * @param managementItems the item list that needs the values
     * @param jsonArray       the json object that includes the values
     */
    private List<HashMap<String, Object>> getValues(HashMap<String, Object> managementItems, JSONArray jsonArray) {
        List<HashMap<String, Object>> itemsList = new ArrayList<HashMap<String, Object>>();

        // Read all rows from data array
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create new list of management items to save a list of values
            HashMap<String, Object> items = new HashMap<String, Object>();
            try {
                JSONObject jsonRow = jsonArray.getJSONObject(i);
                for (String key : managementItems.keySet()) {

                    // Check if the wanted field is in the json object
                    if (jsonRow.has(key)) {
                        // Take matching the value of the json object
                        if (managementItems.get(key).equals(Integer.class)) {
                            items.put(key, jsonRow.getInt(key));
                        } else if (managementItems.get(key).equals(String.class)) {
                            items.put(key, jsonRow.getString(key));
                        } else if (managementItems.get(key).getClass().equals(HashMap.class)) {
                            items.put(key, getValues((HashMap<String, Object>) managementItems.get(key), jsonRow.getJSONArray(key)));
                        } else {
                            // ToDo: Meldung "nicht definierter object typ oder so?
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Add list of management items (more values) to list list
            itemsList.add(items);
        }
        return itemsList;
    }

    /**
     * Calls an other fragment
     *
     * @param fragment the called fragment
     */
    protected void callOtherFragment(Fragment fragment) {
        callOtherFragment(fragment, null);
    }

    /**
     * Calls an other fragment and sends some data
     *
     * @param fragment the called fragment
     * @param args     some data for the new fragment
     */
    protected void callOtherFragment(Fragment fragment, Bundle args) {
        if (args != null) {
            fragment.setArguments(args);
        }
        ((FragmentManager) getFragmentManager()).beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}
