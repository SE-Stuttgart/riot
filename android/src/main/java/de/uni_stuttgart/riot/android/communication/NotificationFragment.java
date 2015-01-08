package de.uni_stuttgart.riot.android.communication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.enpro.android.riot.R;

/**
 * FIXME What is this class doing here?
 */
public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_main, container, false);

		return view;
	}
}
