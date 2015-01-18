/*
 * Copyright 2014 Christophe Beyls
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.gulch.linuxday.android.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.api.LinuxDayApi;
import it.gulch.linuxday.android.constants.ActionConstants;
import it.gulch.linuxday.android.enums.Section;
import it.gulch.linuxday.android.fragments.dialogs.AboutDialogFragment;
import it.gulch.linuxday.android.fragments.dialogs.DownloadScheduleReminderDialogFragment;
import it.gulch.linuxday.android.services.PreferencesService;
import it.gulch.linuxday.android.services.impl.ManagerFactory;
import it.gulch.linuxday.android.tasks.DownloadScheduleAsyncTask;

/**
 * Main entry point of the application. Allows to switch between section fragments and update the database.
 *
 * @author Christophe Beyls
 * @author Paolo Cortis
 */
public class MainActivity extends ActionBarActivity implements ListView.OnItemClickListener
{
	private static final long DATABASE_VALIDITY_DURATION = 24L * 60L * 60L * 1000L; // 24h

	private static final long DOWNLOAD_REMINDER_SNOOZE_DURATION = 24L * 60L * 60L * 1000L; // 24h

	private static final String PREF_LAST_DOWNLOAD_REMINDER_TIME = "last_download_reminder_time";

	private static final String STATE_CURRENT_SECTION = "current_section";

	private static final DateFormat LAST_UPDATE_DATE_FORMAT =
			DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());

	private static final String TAG = MainActivity.class.getSimpleName();

	private Section currentSection;

	private DrawerLayout drawerLayout;

	private ActionBarDrawerToggle drawerToggle;

	private View mainMenu;

	private TextView lastUpdateTextView;

	private MainMenuAdapter menuAdapter;

	private PreferencesService preferencesService;

	private MenuItem refreshItem;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main);

		refreshItem = null;

		setupServices();

		setupDrawer();

		restoreCurrentSection(savedInstanceState);
		setupMainMenu();
		updateActionBar();
	}

	private void setupServices()
	{
		preferencesService = ManagerFactory.getPreferencesService();
	}

	private void setupDrawer()
	{
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(getResources().getDrawable(R.drawable.drawer_shadow), Gravity.LEFT);
		drawerToggle = new DrawerToggle();
		drawerToggle.setDrawerIndicatorEnabled(true);
		drawerLayout.setDrawerListener(drawerToggle);
		// Disable drawerLayout focus to allow trackball navigation.
		// We handle the drawer closing on back press ourselves.
		drawerLayout.setFocusable(false);
	}

	private void restoreCurrentSection(Bundle savedInstanceState)
	{
		boolean shouldRestoreSession = (savedInstanceState == null);
		if(shouldRestoreSession) {
			currentSection = Section.TRACKS;
			String fragmentClassName = currentSection.getFragmentClassName();
			Fragment f = Fragment.instantiate(this, fragmentClassName);
			getSupportFragmentManager().beginTransaction().add(R.id.content, f, fragmentClassName).commit();
		} else {
			currentSection = Section.values()[savedInstanceState.getInt(STATE_CURRENT_SECTION)];
		}
	}

	private void setupMainMenu()
	{
		mainMenu = findViewById(R.id.main_menu);
		ListView menuListView = (ListView) findViewById(R.id.main_menu_list);
		LayoutInflater inflater = LayoutInflater.from(this);
		View menuHeaderView = inflater.inflate(R.layout.header_main_menu, null);
		menuListView.addHeaderView(menuHeaderView, null, false);

		LocalBroadcastManager.getInstance(this).registerReceiver(scheduleRefreshedReceiver, new IntentFilter(
				ActionConstants.ACTION_SCHEDULE_REFRESHED));

		menuAdapter = new MainMenuAdapter(inflater);
		menuListView.setAdapter(menuAdapter);
		menuListView.setOnItemClickListener(this);

		// Last update date, below the menu
		lastUpdateTextView = (TextView) findViewById(R.id.last_update);
		updateLastUpdateTime();

		// Ensure the current section is visible in the menu
		menuListView.setSelection(currentSection.ordinal());
	}

	private void updateLastUpdateTime()
	{
		long lastUpdateTime = preferencesService.getLastUpdateTime(this);

		String lastUpdateTimeAsString;
		if(lastUpdateTime == -1L) {
			lastUpdateTimeAsString = getString(R.string.never);
		} else {
			lastUpdateTimeAsString = LAST_UPDATE_DATE_FORMAT.format(new Date(lastUpdateTime));
		}

		lastUpdateTextView.setText(getString(R.string.last_update, lastUpdateTimeAsString));
	}

	private void updateActionBar()
	{
		getSupportActionBar()
				.setTitle(drawerLayout.isDrawerOpen(mainMenu) ? R.string.app_name : currentSection.getTitleResId());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		if(drawerLayout.isDrawerOpen(mainMenu)) {
			updateActionBar();
		}
		drawerToggle.syncState();
	}

	@Override
	public void onBackPressed()
	{
		if(drawerLayout.isDrawerOpen(mainMenu)) {
			drawerLayout.closeDrawer(mainMenu);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_CURRENT_SECTION, currentSection.ordinal());
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		// Ensure the progress bar is hidden when starting
		//setSupportProgressBarVisibility(false);

		// Monitor the schedule download
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		lbm.registerReceiver(scheduleDownloadProgressReceiver,
							 new IntentFilter(LinuxDayApi.ACTION_DOWNLOAD_SCHEDULE_PROGRESS));
		lbm.registerReceiver(scheduleDownloadResultReceiver,
							 new IntentFilter(LinuxDayApi.ACTION_DOWNLOAD_SCHEDULE_RESULT));

		// Download reminder
		long now = System.currentTimeMillis();
		long time = preferencesService.getLastUpdateTime(this);
		if((time == -1L) || (time < (now - DATABASE_VALIDITY_DURATION))) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			time = prefs.getLong(PREF_LAST_DOWNLOAD_REMINDER_TIME, -1L);
			if((time == -1L) || (time < (now - DOWNLOAD_REMINDER_SNOOZE_DURATION))) {
				prefs.edit().putLong(PREF_LAST_DOWNLOAD_REMINDER_TIME, now).commit();

				FragmentManager fm = getSupportFragmentManager();
				if(fm.findFragmentByTag("download_reminder") == null) {
					new DownloadScheduleReminderDialogFragment().show(fm, "download_reminder");
				}
			}
		}
	}

	@Override
	protected void onStop()
	{
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		lbm.unregisterReceiver(scheduleDownloadProgressReceiver);
		lbm.unregisterReceiver(scheduleDownloadResultReceiver);

		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(scheduleRefreshedReceiver);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);

		final MenuItem searchMenuItem = menu.findItem(R.id.search);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextChange(String newText)
			{
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query)
			{
				MenuItemCompat.collapseActionView(searchMenuItem);
				return false;
			}
		});
		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
		{
			@Override
			public boolean onSuggestionSelect(int position)
			{
				return false;
			}

			@Override
			public boolean onSuggestionClick(int position)
			{
				MenuItemCompat.collapseActionView(searchMenuItem);
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// Hide & disable primary (contextual) action items when the main menu is opened
		if(drawerLayout.isDrawerOpen(mainMenu)) {
			final int size = menu.size();
			for(int i = 0; i < size; ++i) {
				MenuItem item = menu.getItem(i);
				if((item.getOrder() & 0xFFFF0000) == 0) {
					item.setVisible(false).setEnabled(false);
				}
			}
		}

		MenuItem refreshMenuItem = menu.findItem(R.id.refresh);

		boolean isRefreshing = refreshItem != null;
		Log.d(TAG, "isRefreshing: " + Boolean.toString(isRefreshing));
		refreshMenuItem.setEnabled(!isRefreshing);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Will close the drawer if the home button is pressed
		if(drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch(item.getItemId()) {
			case R.id.search:
				return false;
			case R.id.refresh:
				refreshItem = enableRefreshAnimation(item);
				startDownloadSchedule();
				return true;
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				overridePendingTransition(R.anim.slide_in_right, R.anim.partial_zoom_out);
				return true;
			case R.id.about:
				new AboutDialogFragment().show(getSupportFragmentManager(), "about");
				return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	public void startDownloadSchedule()
	{
		// Start by displaying indeterminate progress, determinate will come later
		//setSupportProgressBarIndeterminate(true);
		//setSupportProgressBarVisibility(true);

		DownloadScheduleAsyncTask instance = new DownloadScheduleAsyncTask(this);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			instance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			instance.execute();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Decrease position by 1 since the listView has a header view.
		Section section = menuAdapter.getItem(position - 1);
		if(section != currentSection) {
			// Switch to new section
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			Fragment f = fm.findFragmentById(R.id.content);
			if(f != null) {
				if(currentSection.shouldKeep()) {
					ft.detach(f);
				} else {
					ft.remove(f);
				}
			}
			String fragmentClassName = section.getFragmentClassName();
			if(section.shouldKeep() && ((f = fm.findFragmentByTag(fragmentClassName)) != null)) {
				ft.attach(f);
			} else {
				f = Fragment.instantiate(this, fragmentClassName);
				ft.add(R.id.content, f, fragmentClassName);
			}
			ft.commit();

			currentSection = section;
			menuAdapter.notifyDataSetChanged();
		}

		drawerLayout.closeDrawer(mainMenu);
	}

	private MenuItem enableRefreshAnimation(MenuItem refreshItem)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);

		return MenuItemCompat.setActionView(refreshItem, iv);
	}

	private void disableRefreshAnimation()
	{
		if(refreshItem == null) {
			return;
		}

		View actionView = MenuItemCompat.getActionView(refreshItem);
		if(actionView != null) {
			actionView.clearAnimation();
		}

		MenuItemCompat.setActionView(refreshItem, null);
		refreshItem = null;
	}

	// MAIN MENU
	private class MainMenuAdapter extends BaseAdapter
	{
		private Section[] sections = Section.values();

		private LayoutInflater inflater;

		private int currentSectionBackgroundColor;

		public MainMenuAdapter(LayoutInflater inflater)
		{
			this.inflater = inflater;
			currentSectionBackgroundColor = getResources().getColor(R.color.translucent_grey);
		}

		@Override
		public int getCount()
		{
			return sections.length;
		}

		@Override
		public Section getItem(int position)
		{
			return sections[position];
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.item_main_menu, parent, false);
			}

			Section section = getItem(position);

			TextView tv = (TextView) convertView.findViewById(R.id.section_text);
			tv.setText(section.getTitleResId());
			tv.setCompoundDrawablesWithIntrinsicBounds(section.getIconResId(), 0, 0, 0);
			// Show highlighted background for current section
			tv.setBackgroundColor((section == currentSection) ? currentSectionBackgroundColor : Color.TRANSPARENT);

			return convertView;
		}
	}

	private final BroadcastReceiver scheduleDownloadProgressReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//			setSupportProgressBarIndeterminate(false);
			setSupportProgress(intent.getIntExtra(LinuxDayApi.EXTRA_PROGRESS, 0) * 100);
		}
	};

	private final BroadcastReceiver scheduleDownloadResultReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Hide the progress bar with a fill and fade out animation
			//setSupportProgressBarIndeterminate(false);
			//setSupportProgress(10000);
			disableRefreshAnimation();
			supportInvalidateOptionsMenu();

			int result = intent.getIntExtra(LinuxDayApi.EXTRA_RESULT, LinuxDayApi.RESULT_ERROR);
			if(result == LinuxDayApi.RESULT_ERROR) {
				Toast.makeText(MainActivity.this, R.string.schedule_loading_error, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.events_download_completed, result),
							   Toast.LENGTH_LONG).show();
			}
		}
	};

	private final BroadcastReceiver scheduleRefreshedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			updateLastUpdateTime();
		}
	};

	private class DrawerToggle extends ActionBarDrawerToggle
	{
		public DrawerToggle()
		{
			super(MainActivity.this, MainActivity.this.drawerLayout, R.string.main_menu, R.string.close_menu);
		}

		@Override
		public void onDrawerOpened(View drawerView)
		{
			updateActionBar();
			disableRefreshAnimation();
			supportInvalidateOptionsMenu();
			// Make keypad navigation easier
			mainMenu.requestFocus();
		}

		@Override
		public void onDrawerClosed(View drawerView)
		{
			updateActionBar();
			disableRefreshAnimation();
			supportInvalidateOptionsMenu();
		}
	}
}
