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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.EventDetailsActivity;
import it.gulch.linuxday.android.adapters.EventsAdapter;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.PersonEventsLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Person;

public class PersonInfoListFragment extends ListFragment
{
	private static final int PERSON_EVENTS_LOADER_ID = 1;

	private static final String ARG_PERSON = "person";

	private static final String TAG = PersonsListFragment.class.getSimpleName();

	private Person person;

	private EventsAdapter adapter;

	private List<Event> events;

	private EventManager eventManager;

	LoaderCallbacks<List<Event>> loaderCallbacks;

	public static PersonInfoListFragment newInstance(Person person)
	{
		PersonInfoListFragment f = new PersonInfoListFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_PERSON, person);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		events = new ArrayList<Event>();

		adapter = new EventsAdapter(getActivity(), events);
		person = (Person) getArguments().getSerializable(ARG_PERSON);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.person, menu);
	}

	// FIXME
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item)
	//	{
	//		switch(item.getItemId()) {
	//			case R.id.more_info:
	//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(person.getUrl()));
	//				startActivity(intent);
	//				return true;
	//		}
	//		return false;
	//	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.no_data));

		int contentMargin = getResources().getDimensionPixelSize(R.dimen.content_margin);
		ListView listView = getListView();
		listView.setPadding(contentMargin, contentMargin, contentMargin, contentMargin);
		listView.setClipToPadding(false);
		listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);

		View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_person_info, null);
		((TextView) headerView.findViewById(R.id.title)).setText(person.getCompleteName(Person.CompleteNameEnum.NAME_FIRST));
		listView.addHeaderView(headerView, null, false);

		setListAdapter(adapter);
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
				return new PersonEventsLoader(getActivity(), eventManager, person);
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
		getLoaderManager().initLoader(PERSON_EVENTS_LOADER_ID, null, loaderCallbacks);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Event event = adapter.getItem(position - 1);
		Intent intent =
				new Intent(getActivity(), EventDetailsActivity.class).putExtra(EventDetailsActivity.EXTRA_EVENT,
																			   event);
		startActivity(intent);
	}
}
