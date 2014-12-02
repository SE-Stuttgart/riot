package de.enpro.android_riot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {

	private TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.fragment_layout, container, false);
		String menu = getArguments().getString("Menu");
		textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(menu);
		
		return view;
	}
}