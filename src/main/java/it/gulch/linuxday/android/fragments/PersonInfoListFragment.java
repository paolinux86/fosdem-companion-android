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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.EventDetailsActivity;
import it.gulch.linuxday.android.adapters.EventsAdapter;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Person;

@EFragment
public class PersonInfoListFragment extends ListFragment implements LoaderCallbacks<List<Event>>
{
	private static final int PERSON_EVENTS_LOADER_ID = 1;

	private static final String ARG_PERSON = "person";

	private Person person;

	private EventsAdapter adapter;

	private List<Event> events;

	@Bean(EventManagerImpl.class)
	EventManager eventManager;

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
		((TextView) headerView.findViewById(R.id.title)).setText(person.getName());
		listView.addHeaderView(headerView, null, false);

		setListAdapter(adapter);
		setListShown(false);

		getLoaderManager().initLoader(PERSON_EVENTS_LOADER_ID, null, this);
	}

	private class PersonEventsLoader extends SimpleDatabaseLoader<List<Event>>
	{
		private final Person person;

		public PersonEventsLoader(Context context, Person person)
		{
			super(context);
			this.person = person;
		}

		@Override
		protected List<Event> getObject()
		{
			try {
				return eventManager.searchEventsByPerson(person);
			} catch(SQLException e) {
				return Collections.emptyList();
			}
		}
	}

	@Override
	public Loader<List<Event>> onCreateLoader(int id, Bundle args)
	{
		return new PersonEventsLoader(getActivity(), person);
	}

	@Override
	public void onLoadFinished(Loader<List<Event>> loader, List<Event> data)
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
	public void onLoaderReset(Loader<List<Event>> loader)
	{
		events.clear();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Event event = adapter.getItem(position - 1);
		Intent intent =
			new Intent(getActivity(), EventDetailsActivity.class).putExtra(EventDetailsActivity.EXTRA_EVENT, event);
		startActivity(intent);
	}
}
