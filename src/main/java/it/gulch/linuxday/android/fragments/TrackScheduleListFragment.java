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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.adapters.TrackScheduleAdapter;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Track;

@EFragment
public class TrackScheduleListFragment extends ListFragment implements Handler.Callback, LoaderCallbacks<List<Event>>
{
	@Bean(EventManagerImpl.class)
	EventManager eventManager;

	private static final int EVENTS_LOADER_ID = 1;

	private static final int REFRESH_TIME_WHAT = 1;

	private static final long REFRESH_TIME_INTERVAL = 60 * 1000L; // 1min

	private static final String ARG_DAY = "day";

	private static final String ARG_TRACK = "track";

	private static final String ARG_FROM_EVENT_ID = "from_event_id";

	private Day day;

	private Handler handler;

	private TrackScheduleAdapter adapter;

	private Callbacks listener;

	private boolean selectionEnabled = false;

	private boolean isListAlreadyShown = false;

	private List<Event> events;

	public static TrackScheduleListFragment newInstance(Day day, Track track)
	{
		TrackScheduleListFragment f = new TrackScheduleListFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_DAY, day);
		args.putSerializable(ARG_TRACK, track);
		f.setArguments(args);
		return f;
	}

	public static TrackScheduleListFragment newInstance(Day day, Track track, long fromEventId)
	{
		Bundle args = new Bundle();
		args.putSerializable(ARG_DAY, day);
		args.putSerializable(ARG_TRACK, track);
		args.putLong(ARG_FROM_EVENT_ID, fromEventId);

		TrackScheduleListFragment f = new TrackScheduleListFragment();
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		day = (Day) getArguments().getSerializable(ARG_DAY);
		handler = new Handler(this);

		events = new ArrayList<Event>();

		adapter = new TrackScheduleAdapter(getActivity(), events);
		setListAdapter(adapter);

		if(savedInstanceState != null) {
			isListAlreadyShown = savedInstanceState.getBoolean("isListAlreadyShown");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putBoolean("isListAlreadyShown", isListAlreadyShown);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if(activity instanceof Callbacks) {
			listener = (Callbacks) activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		listener = null;
	}

	private void notifyEventSelected(int position)
	{
		if(listener != null) {
			listener.onEventSelected(position,
									 (position == ListView.INVALID_POSITION) ? null : adapter.getItem(position));
		}
	}

	public void setSelectionEnabled(boolean selectionEnabled)
	{
		this.selectionEnabled = selectionEnabled;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(selectionEnabled ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
		setEmptyText(getString(R.string.no_data));
		setListShown(false);

		getLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// Setup display auto-refresh during the track's day
		long now = System.currentTimeMillis();
		long dayStart = day.getDayDate().getTime();
		if(now < dayStart) {
			// Before track day, schedule refresh in the future
			handler.sendEmptyMessageDelayed(REFRESH_TIME_WHAT, dayStart - now);
		} else if(now < dayStart + android.text.format.DateUtils.DAY_IN_MILLIS) {
			// During track day, start refresh immediately
			adapter.setCurrentTime(now);
			handler.sendEmptyMessageDelayed(REFRESH_TIME_WHAT, REFRESH_TIME_INTERVAL);
		} else {
			// After track day, disable refresh
			adapter.setCurrentTime(-1);
		}
	}

	@Override
	public void onStop()
	{
		handler.removeMessages(REFRESH_TIME_WHAT);
		super.onStop();
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what) {
			case REFRESH_TIME_WHAT:
				adapter.setCurrentTime(System.currentTimeMillis());
				handler.sendEmptyMessageDelayed(REFRESH_TIME_WHAT, REFRESH_TIME_INTERVAL);
				return true;
		}
		return false;
	}

	@Override
	public Loader<List<Event>> onCreateLoader(int id, Bundle args)
	{
		Day day = (Day) getArguments().getSerializable(ARG_DAY);
		Track track = (Track) getArguments().getSerializable(ARG_TRACK);
		return new TrackScheduleLoader(getActivity(), track);
	}

	@Override
	public void onLoadFinished(Loader<List<Event>> loader, List<Event> data)
	{
		if(data != null) {
			events.clear();
			events.addAll(data);
			adapter.notifyDataSetChanged();

			if(selectionEnabled) {
				final int count = adapter.getCount();
				int checkedPosition = getListView().getCheckedItemPosition();
				if((checkedPosition == ListView.INVALID_POSITION) || (checkedPosition >= count)) {
					// There is no current valid selection, use the default one
					checkedPosition = getDefaultPosition();
					if(checkedPosition != ListView.INVALID_POSITION) {
						getListView().setItemChecked(checkedPosition, true);
					}
				}

				// Ensure the current selection is visible
				if(checkedPosition != ListView.INVALID_POSITION) {
					setSelection(checkedPosition);
				}
				// Notify the parent of the current selection to synchronize its state
				notifyEventSelected(checkedPosition);
			} else if(!isListAlreadyShown) {
				int position = getDefaultPosition();
				if(position != ListView.INVALID_POSITION) {
					setSelection(position);
				}
			}
		}

		// The list should now be shown.
		if(isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	/**
	 * @return The default position in the list, or -1 if the list is empty
	 */
	private int getDefaultPosition()
	{
		final int count = adapter.getCount();
		if(count == 0) {
			return ListView.INVALID_POSITION;
		}

		long fromEventId = getArguments().getLong(ARG_FROM_EVENT_ID, -1L);
		if(fromEventId != -1L) {
			// Look for the source event in the list and return its position
			for(int i = 0; i < count; ++i) {
				if(adapter.getItemId(i) == fromEventId) {
					return i;
				}
			}
		}

		return 0;
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
		notifyEventSelected(position);
	}

	private class TrackScheduleLoader extends SimpleDatabaseLoader<List<Event>>
	{
		private final Track track;

		public TrackScheduleLoader(Context context, Track track)
		{
			super(context);
			this.track = track;
		}

		@Override
		protected List<Event> getObject()
		{
			try {
				return eventManager.searchEventsByTrack(track);
			} catch(SQLException e) {
				return Collections.emptyList();
			}
		}
	}

	/**
	 * Interface implemented by container activities
	 */
	public interface Callbacks
	{
		void onEventSelected(int position, Event event);
	}
}
