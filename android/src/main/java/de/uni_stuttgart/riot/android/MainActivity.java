package de.uni_stuttgart.riot.android;

import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.account.AccountFragment;
import de.uni_stuttgart.riot.android.database.LanguageDatabase;
import de.uni_stuttgart.riot.android.language.LanguageFragment;

/**
 * The main window.
 */
public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    private LanguageDatabase dbHandler;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
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
    }

    private void setLanguage() {
        dbHandler = new LanguageDatabase(this);
        if (dbHandler.getCount() == 0) {
            locale = new Locale("en");
        } else {
            locale = new Locale(dbHandler.getLanguage());
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    // Prepare the OptionsMenu / Refreshbutton on the right side.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Define displaying settings for the options menu.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        // Refresh button is only shown in the home screen
        if (!getActionBar().getTitle().equals("Home")) {
            menu.findItem(R.id.action_refresh).setVisible(false);
        } else {
            menu.findItem(R.id.action_refresh).setVisible(true);
        }

        // menu.findItem(R.id.action_settings).setVisible(!homeScreenNotDisplayed);

        return super.onPrepareOptionsMenu(menu);
    }

    // Actions for the option buttons.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        case R.id.action_refresh:
            // TODO: refresh
            // Toast.makeText(getApplicationContext(),
            // "TODO: implement refresh",
            // Toast.LENGTH_LONG).show();
            getEventList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * TODO Missing description.
     */
    private void getEventList() {
        ArrayList<String> meineListe = new ArrayList<String>();
        meineListe.add("TEST");
        meineListe.add("TEST1");
        meineListe.add("TEST2");
        ListAdapter listenAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meineListe);
        ListView meineListView = (ListView) findViewById(R.id.LISTE);
        meineListView.setAdapter(listenAdapter);
    }

    private void selectItem(int position) {
        Fragment fragment;

        // Opens the main fragment
        if (position == 0) {
            fragment = new MyFragment();
            startFragment(position, fragment);
        }

        // Opens the filter fragment
        if (position == 1) {
            // TODO: Filter Fragment
        }

        // Opens the account fragment
        if (position == 2) {
            fragment = new AccountFragment();
            startFragment(position, fragment);
        }

        // Opens the language fragment
        if (position == 3) {
            fragment = new LanguageFragment();
            startFragment(position, fragment);
        }

        // Opens the reset fragment
        if (position == 4) { // NOCS FIXME There must be a better way for this.
            // TODO: Reset settings (delete Account, delete Filter settings, and
            // so on)
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    // When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...

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

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);

        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
