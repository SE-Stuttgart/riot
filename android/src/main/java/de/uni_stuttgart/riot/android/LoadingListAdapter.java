package de.uni_stuttgart.riot.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This is a {@link ListAdapter} that displays a loading message as its only entry. It can be used by activities before the actual data has
 * been loaded.
 * 
 * @author Philipp Keck
 */
public class LoadingListAdapter extends BaseAdapter {

    /**
     * The only list item.
     */
    private static final Object DUMMY = new Object();

    private final Context context;

    /**
     * Creates a new instance.
     * 
     * @param context
     *            The Android context.
     */
    public LoadingListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return DUMMY;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position != 0) {
            return null;
        } else if (convertView != null) {
            return convertView;
        }

        TextView text = new TextView(context);
        text.setText(context.getString(R.string.pleaseWait));
        return text;

    }

}
