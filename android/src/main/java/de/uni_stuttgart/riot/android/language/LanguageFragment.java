package de.uni_stuttgart.riot.android.language;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.LanguageDatabase;

public class LanguageFragment extends Fragment {

	private LanguageDatabase dbHandler;
	private RadioGroup buttonGroup;

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
					dbHandler = new LanguageDatabase(getActivity());
					dbHandler.deleteAllLanguages();
					dbHandler.addLanguage("en", "Supports the english language");
					break;

				case R.id.lang_de:
					dbHandler = new LanguageDatabase(getActivity());
					dbHandler.deleteAllLanguages();
					dbHandler.addLanguage("de", "Supports the german language");
					break;
				default:
					break;
				}
			}
		});

		return view;
	}
}