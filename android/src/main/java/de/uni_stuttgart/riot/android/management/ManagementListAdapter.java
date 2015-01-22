package de.uni_stuttgart.riot.android.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Benny on 09.01.2015.
 */
public class ManagementListAdapter extends ArrayAdapter<HashMap<String, Object>> {

    private ManagementListFragment managementListFragment;
    private Context context;
    private int resource;
    private List<HashMap<String, Object>> itemsList;


    /**
     * Constructor
     *
     * @param context   is the calling context
     * @param itemsList includes the data that will be shown by the list
     */
    public ManagementListAdapter(ManagementListFragment managementListFragment, Context context, int resource, List<HashMap<String, Object>> itemsList) {
        super(context, resource, itemsList);
        this.managementListFragment = managementListFragment;
        this.context = context;
        this.resource = resource;
        this.itemsList = itemsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get current row view
        View listRow = convertView;
        if (listRow == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listRow = inflater.inflate(resource, parent, false);
        }

        // Save all elements of an item
        managementListFragment.doGetView(listRow, itemsList.get(position));
        return listRow;
    }
}
