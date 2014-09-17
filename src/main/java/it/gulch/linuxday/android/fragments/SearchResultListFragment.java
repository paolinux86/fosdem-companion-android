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
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.EventDetailsActivity;
import it.gulch.linuxday.android.adapters.EventsAdapter;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Event;

public class SearchResultListFragment extends ListFragment implements LoaderCallbacks<List<Event>>
{
	private static final int EVENTS_LOADER_ID = 1;

	private static final String ARG_QUERY = "query";

	private static final String TAG = SearchResultListFragment.class.getSimpleName();

	private List<Event> events;

	private EventsAdapter adapter;

	private EventManager eventManager;

	public static SearchResultListFragment newInstance(String query)
	{
		SearchResultListFragment f = new SearchResultListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_QUERY, query);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		events = new ArrayList<Event>();

		adapter = new EventsAdapter(getActivity(), events);
		setListAdapter(adapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.no_search_result));
		setListShown(false);

		getLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
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
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private class TextSearchLoader extends SimpleDatabaseLoader<List<Event>>
	{
		private final String query;

		public TextSearchLoader(Context context, String query)
		{
			super(context);
			this.query = query;
		}

		@Override
		protected List<Event> getObject()
		{
			return eventManager.search(query);
		}
	}

	@Override
	public Loader<List<Event>> onCreateLoader(int id, Bundle args)
	{
		String query = getArguments().getString(ARG_QUERY);
		return new TextSearchLoader(getActivity(), query);
	}

	@Override
	public void onLoadFinished(Loader<List<Event>> loader, List<Event> data)
	{
		if(data != null) {
			events.clear();
			events.addAll(data);
			adapter.notifyDataSetChanged();;
		}

		// The list should now be shown.
		if(isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Event>> loader)
	{
		events.clear();
		adapter.notifyDataSetChanged();;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Event event = adapter.getItem(position);
		Intent intent =
			new Intent(getActivity(), EventDetailsActivity.class).putExtra(EventDetailsActivity.EXTRA_EVENT, event);
		startActivity(intent);
	}
}
