package de.uni_stuttgart.riot.android.communication;

import java.util.ArrayList;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.MainActivity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ServerConnection extends AsyncTask<Void, Void, Greeting> {

	private MainActivity mA;

	public ServerConnection(MainActivity mA) {
		this.mA = mA;
	}

	@Override
	protected Greeting doInBackground(Void... params) {
		try {
			final String url = "http://rest-service.guides.spring.io/greeting";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			Greeting greeting = restTemplate.getForObject(url, Greeting.class);
			return greeting;
		} catch (Exception e) {
			System.out.println("fehler");
			// Log.e("MainActivity", e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Greeting greeting) {
		
		ArrayList<String> meineListe = new ArrayList<String>();
		meineListe.add(greeting.getContent());
		meineListe.add(greeting.getId());

		ListAdapter listenAdapter = new ArrayAdapter<String>(
				mA, android.R.layout.simple_list_item_1,
				meineListe);
		ListView meineListView = (ListView) mA.findViewById(R.id.LISTE);
		meineListView.setAdapter(listenAdapter);

	}

}
