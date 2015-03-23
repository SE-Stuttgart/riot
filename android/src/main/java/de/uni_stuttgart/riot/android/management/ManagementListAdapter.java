package de.uni_stuttgart.riot.android.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Is the list adapter for the list for the ManagementListFragment class.
 *
 * @author Benny
 */
public class ManagementListAdapter extends ArrayAdapter<Object> {

    private ManagementListActivity managementListActivity;
    private Context context;
    private int resource;
    private List<Object> data;

    /**
     * Constructor.
     *
     * @param managementListActivity is the fragment where the adapter was added
     * @param context                is the application context
     * @param resource               it the resource id of the layout for the list item
     * @param data                   is a list of data that will be displayed in the list
     */
    public ManagementListAdapter(ManagementListActivity managementListActivity, Context context, int resource, List<Object> data) {
        super(context, resource, data);
        this.managementListActivity = managementListActivity;
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get current row view
        View listRow = convertView;
        if (listRow == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listRow = inflater.inflate(resource, parent, false);
        }

        // Set values of that item to the view elements
        managementListActivity.doGetView(listRow, data.get(position));
        return listRow;
    }
}
