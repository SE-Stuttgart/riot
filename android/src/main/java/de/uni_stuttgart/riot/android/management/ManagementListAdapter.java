package de.uni_stuttgart.riot.android.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Is the list adapter for the list for the ManagementListActivity class.
 *
 * @param <T> type of the handled object
 * @author Benny
 */
public class ManagementListAdapter<T> extends ArrayAdapter<T> {

    private ManagementListActivity<T, ?> managementListActivity;
    private int resource;
    private List<T> itemList;

    /**
     * Constructor.
     *
     * @param managementListActivity is the fragment where the adapter was added
     * @param resource               it the resource id of the layout for the list item
     * @param itemList               is a list of items that will be displayed in the list
     */
    public ManagementListAdapter(ManagementListActivity<T, ?> managementListActivity, int resource, List<T> itemList) {
        super(managementListActivity.getApplicationContext(), resource, itemList);
        this.managementListActivity = managementListActivity;
        this.resource = resource;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get current row view
        View listRow = convertView;
        if (listRow == null) {
            LayoutInflater inflater = (LayoutInflater) managementListActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listRow = inflater.inflate(resource, parent, false);
        }
        // Get item from the item list
        T item = itemList.get(position);

        // Get values of the item and save them
        managementListActivity.buildListItem(item, listRow);

        return listRow;
    }
}
