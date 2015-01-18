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
package it.gulch.linuxday.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.EventDetailsActivity;
import it.gulch.linuxday.android.adapters.EventsAdapter;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.BookmarksLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.widgets.BookmarksMultiChoiceModeListener;

/**
 * Bookmarks list, optionally filterable.
 *
 * @author Christophe Beyls
 */
public class BookmarksListFragment extends ListFragment
{
	private static final int BOOKMARKS_LOADER_ID = 1;

	private static final String PREF_UPCOMING_ONLY = "bookmarks_upcoming_only";

	private static final String TAG = BookmarksListFragment.class.getSimpleName();

	private EventsAdapter adapter;

	private boolean upcomingOnly;

	private MenuItem filterMenuItem;

	private MenuItem upcomingOnlyMenuItem;

	private List<Event> events;

	private EventManager eventManager;

	private BookmarkManager bookmarkManager;

	private LoaderCallbacks<List<Event>> loaderCallbacks;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		events = new ArrayList<>();

		adapter = new EventsAdapter(getActivity(), events);
		setListAdapter(adapter);

		upcomingOnly = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_UPCOMING_ONLY, false);

		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			BookmarksMultiChoiceModeListener.register(getListView(), bookmarkManager);
		}

		setEmptyText(getString(R.string.no_bookmark));
		setListShown(false);

		setupLoaderCallbacks();
	}

	private void setupLoaderCallbacks()
	{
		loaderCallbacks = new LoaderCallbacks<List<Event>>()
		{
			@Override
			public Loader<List<Event>> onCreateLoader(int i, Bundle bundle)
			{
				return new BookmarksLoader(getActivity(), eventManager, upcomingOnly);
			}

			@Override
			public void onLoadFinished(Loader<List<Event>> listLoader, List<Event> data)
			{
				if(data != null) {
					events.clear();
					events.addAll(data);
					adapter.notifyDataSetChanged();
				}

				// The list should now be shown.
				if(isResumed()) {
					setListShown(true);
				} else {
					setListShownNoAnimation(true);
				}
			}

			@Override
			public void onLoaderReset(Loader<List<Event>> listLoader)
			{
				events.clear();
				adapter.notifyDataSetChanged();
			}
		};
		getLoaderManager().initLoader(BOOKMARKS_LOADER_ID, null, loaderCallbacks);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		setupServices(activity);
	}

	private void setupServices(Activity activity)
	{
		try {
			eventManager = DatabaseManagerFactory.getEventManager(activity);
			bookmarkManager = DatabaseManagerFactory.getBookmarkManager(activity);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.bookmarks, menu);
		filterMenuItem = menu.findItem(R.id.filter);
		upcomingOnlyMenuItem = menu.findItem(R.id.upcoming_only);
		updateOptionsMenu();
	}

	private void updateOptionsMenu()
	{
		if(filterMenuItem != null) {
			filterMenuItem.setIcon(upcomingOnly ? R.drawable.ic_action_filter_selected : R.drawable.ic_action_filter);
			upcomingOnlyMenuItem.setChecked(upcomingOnly);
		}
	}

	@Override
	public void onDestroyOptionsMenu()
	{
		super.onDestroyOptionsMenu();
		filterMenuItem = null;
		upcomingOnlyMenuItem = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.upcoming_only:
				upcomingOnly = !upcomingOnly;
				updateOptionsMenu();
				getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(PREF_UPCOMING_ONLY, upcomingOnly)
						.commit();
				getLoaderManager().restartLoader(BOOKMARKS_LOADER_ID, null, loaderCallbacks);
				return true;
		}
		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Event event = adapter.getItem(position);
		Intent intent =
				new Intent(getActivity(), EventDetailsActivity.class).putExtra(EventDetailsActivity.EXTRA_EVENT,
																			   event);
		startActivity(intent);
	}
}
