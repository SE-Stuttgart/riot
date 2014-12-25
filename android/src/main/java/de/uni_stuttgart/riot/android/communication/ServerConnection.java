package de.uni_stuttgart.riot.android.communication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.MainActivity;
import de.uni_stuttgart.riot.android.NotificationAdapter;
import de.uni_stuttgart.riot.android.NotificationType;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class ServerConnection extends AsyncTask<Void, Void, List<Notification>> {

	private MainActivity mainActivity;

	public ServerConnection(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected List<Notification> doInBackground(Void... params) {
		try {
			final String url = "http://rest-service.guides.spring.io/greeting";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			Notification testNotification = restTemplate.getForObject(url,
					Notification.class);

			List<Notification> notificationList = new ArrayList<Notification>();

			if (testNotification == null) {
				Toast.makeText(mainActivity.getApplicationContext(),
						"Server Connection Error", 5).show();
			} else {

				Notification n1 = new Notification();
				n1.setTitle("FIRE!!!");
				n1.setType(NotificationType.ERROR);
				n1.setId(testNotification.getId());
				n1.setContent(testNotification.getContent());
				n1.setDate(new Date());
				notificationList.add(n1);
				
				Notification n2 = new Notification();
				n2.setTitle("Work starts at 08:00");
				n2.setType(NotificationType.APPOINTMENT);
				n2.setId(testNotification.getId());
				n2.setContent(testNotification.getContent());
				n2.setDate(new Date());
				notificationList.add(n2);
				
				Notification n3 = new Notification();
				n3.setTitle("Refuel car.");
				n3.setType(NotificationType.NOTIFCATION);
				n3.setId(testNotification.getId());
				n3.setContent(testNotification.getContent());
				n3.setDate(new Date());
				notificationList.add(n3);
			
				return notificationList;
			}

			
		} catch (HttpClientErrorException e) {
			Log.e("MainActivity", e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Notification> notificationList) {
		NotificationAdapter chapterListAdapter = new NotificationAdapter(
				mainActivity, notificationList);
		ListView notification = (ListView) mainActivity
				.findViewById(R.id.NotificationList);
		notification.setAdapter(chapterListAdapter);
	}

}
