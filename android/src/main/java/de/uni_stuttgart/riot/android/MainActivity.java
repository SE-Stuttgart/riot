package de.uni_stuttgart.riot.android;

import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.account.AccountFragment;
import de.uni_stuttgart.riot.android.communication.NotificationFragment;
import de.uni_stuttgart.riot.android.communication.ServerConnection;
import de.uni_stuttgart.riot.android.database.FilterDataObjects;
import de.uni_stuttgart.riot.android.filter.FilterFragment;
import de.uni_stuttgart.riot.android.language.LanguageFragment;

/**
 * The main window.
 */
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ListView notificationList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private String[] mMenuTitles;

	private Locale locale;

	private FilterDataObjects filterObjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Database stuff
		//this.deleteDatabase("Database");
		filterObjects = new FilterDataObjects(this);

		// Sets the language
		setLanguage();

		setContentView(R.layout.activity_main);

		mTitle = getTitle();
		mMenuTitles = getResources().getStringArray(R.array.menu_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mMenuTitles));

		// ClickListener for the left ActionBar
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Settings");
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

		// ClickListener for the Notification List
		notificationList = (ListView) findViewById(R.id.NotificationList);
		notificationList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println("click");

			}
		});

		// get the latest Notifications
		new ServerConnection(this, filterObjects).execute();
	}


	/*
	 * ----------------- REFRESH BUTTON -----------------
	 */

	/**
	 * Prepare the refresh button on the right side
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Define displaying settings for the refresh button
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// //get the values for the each filter from the database
		menu.findItem(R.id.filter_error).setChecked(
				filterObjects.getFilterStatus(NotificationType.ERROR));
		menu.findItem(R.id.filter_warning).setChecked(
				filterObjects.getFilterStatus(NotificationType.WARNING));
		menu.findItem(R.id.filter_appointment).setChecked(
				filterObjects.getFilterStatus(NotificationType.APPOINTMENT));

		// Refresh button, filter buttons and the notification list is only
		// shown in the home screen
		if (!getActionBar().getTitle().equals("Home")) {
			menu.findItem(R.id.action_refresh).setVisible(false);
			for (int i = 0; i < menu.size(); i++) {
				menu.getItem(i).setVisible(false);
			}
			notificationList.setAdapter(null);
		} else {
			menu.findItem(R.id.action_refresh).setVisible(true);
			filterObjects.getDatabase().filterNotifications();
		}

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Actions for the refresh button (right upper corner). It can later be
	 * extended with more options.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.filter_error:
			filterObjects.setFilter(new Filter(1, item, NotificationType.ERROR,
					false));
			return true;
		case R.id.filter_appointment:
			filterObjects.setFilter(new Filter(2, item,
					NotificationType.APPOINTMENT, false));
			return true;
		case R.id.filter_warning:
			filterObjects.setFilter(new Filter(3, item,
					NotificationType.WARNING, false));
			return true;
		case R.id.action_refresh:
			new ServerConnection(this, filterObjects).execute();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * ----------------- OPTIONS MENU -----------------
	 */

	/**
	 * Method for changing the language
	 */
	private void setLanguage() {
		if (filterObjects.getDatabase().getCount() == 0) {
			locale = new Locale("en");
		} else {
			locale = new Locale(filterObjects.getDatabase().getLanguage());
		}
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Fragment fragment;

		// Opens the main fragment
		if (position == 0) {
			fragment = new NotificationFragment();
			startFragment(position, fragment);
			filterObjects.getDatabase().filterNotifications();
		}

		// Opens the filter fragment
		if (position == 1) {
			fragment = new FilterFragment(filterObjects);
			startFragment(position, fragment);
		}

		// Opens the account fragment
		if (position == 2) {
			fragment = new AccountFragment();
			startFragment(position, fragment);
		}

		// Opens the language fragment
		if (position == 3) {
			fragment = new LanguageFragment(filterObjects);
			startFragment(position, fragment);
		}

		// Opens the reset fragment
		if (position == 4) {
			// TODO: Reset settings (delete Account, delete Filter settings, and
			// so on)
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void startFragment(int position, Fragment fragment) {
		Bundle args = new Bundle();
		FragmentManager fragmentManager = getFragmentManager();

		args.putString("Menu", mMenuTitles[position]);
		fragment.setArguments(args);

		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);
		setTitle(mMenuTitles[position]);

		mDrawerLayout.closeDrawer(mDrawerList);
	}
}
