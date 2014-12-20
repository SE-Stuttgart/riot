package de.uni_stuttgart.riot.android.communication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.CodeLearnAdapter;
import de.uni_stuttgart.riot.android.MainActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ServerConnection extends AsyncTask<Void, Void, TestString> {

	private MainActivity mainActivity;

	public ServerConnection(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected TestString doInBackground(Void... params) {
		try {
			final String url = "http://rest-service.guides.spring.io/greeting";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			TestString greeting = restTemplate.getForObject(url,
					TestString.class);
			return greeting;
		} catch (Exception e) {
			Log.e("MainActivity", e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(TestString greeting) {

		// ArrayList<String> jsonTest = new ArrayList<String>();
		// jsonTest.add(greeting.getContent());
		// jsonTest.add(greeting.getId());
		//
		// ListView notificationList = (ListView) mainActivity
		// .findViewById(R.id.NotificationList);
		//
		// ListAdapter listAdapter = new ArrayAdapter<String>(mainActivity,
		// android.R.layout.simple_list_item_single_choice, jsonTest);
		//
		// notificationList.setAdapter(listAdapter);

		CodeLearnAdapter chapterListAdapter = new CodeLearnAdapter(
				mainActivity, getDataForListView(greeting));
		ListView codeLearnLessons = (ListView) mainActivity
				.findViewById(R.id.NotificationList);
		codeLearnLessons.setAdapter(chapterListAdapter);
	}

	public List<TestString> getDataForListView(TestString chapter) {
		List<TestString> codeLearnChaptersList = new ArrayList<TestString>();

		for (int i = 0; i < 3; i++) {

			chapter.setId(chapter.getId());
			chapter.setContent(chapter.getContent());
			codeLearnChaptersList.add(chapter);
		}

		return codeLearnChaptersList;

	}

}
