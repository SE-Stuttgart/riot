package de.uni_stuttgart.riot.android.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Created by Benny on 09.01.2015.
 */
public class ManagementListAdapter extends ArrayAdapter<User> {

    private ManagementFragment managementFragment;
    private Context context;
    private int resource;
    private ArrayList<User> itemList;


    /**
     * Constructor
     *
     * @param context   is the calling context
     * @param itemList includes the data that will be shown by the list
     */
    public ManagementListAdapter(ManagementFragment managementFragment, Context context, int resource, ArrayList<User> itemList) {
        super(context, resource, itemList);
        this.managementFragment = managementFragment;
        this.context = context;
        this.resource = resource;
        this.itemList = itemList;
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
        managementFragment.doGetView(listRow, itemList.get(position));
        return listRow;
    }
}
