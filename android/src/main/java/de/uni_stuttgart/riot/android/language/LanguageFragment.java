package de.uni_stuttgart.riot.android.language;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.FilterDataObjects;

/**
 * UI for setting the language.
 */
public class LanguageFragment extends Fragment {
	private FilterDataObjects filterObjects;
	private RadioGroup buttonGroup;

	public LanguageFragment(FilterDataObjects filterObjects) {
		this.filterObjects = filterObjects;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_language, container,
				false);

		buttonGroup = (RadioGroup) view.findViewById(R.id.lang_group);

		buttonGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.lang_en:


					filterObjects.getDatabase().deleteAllLanguages();
					filterObjects.getDatabase().addLanguage("en",
							"Supports the english language");
					restartActivity();
					break;

				case R.id.lang_de:

					filterObjects.getDatabase().deleteAllLanguages();
					filterObjects.getDatabase().addLanguage("de",
							"Supports the german language");
					restartActivity();
					break;
				default:
					break;
				}
			}
		});

		return view;
	}


	private void restartActivity() {
		Intent intent = getActivity().getIntent();
		getActivity().finish();
		getActivity().startActivity(intent);
	}
}
