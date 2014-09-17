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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.viewpagerindicator.PageIndicator;

import org.apache.commons.collections4.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.fragments.EventDetailsFragment;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Track;
import it.gulch.linuxday.android.utils.NfcUtils;

/**
 * Event view of the track schedule; allows to slide between events of the same track using a ViewPager.
 *
 * @author Christophe Beyls
 */
public class TrackScheduleEventActivity extends ActionBarActivity
	implements LoaderCallbacks<List<Event>>, NfcUtils.CreateNfcAppDataCallback
{
	public static final String EXTRA_DAY = "day";

	public static final String EXTRA_TRACK = "track";

	public static final String EXTRA_POSITION = "position";

	private static final int EVENTS_LOADER_ID = 1;

	private static final String TAG = TrackScheduleEventActivity.class.getSimpleName();

	private Track track;

	private int initialPosition = -1;

	private View progress;

	private ViewPager pager;

	private PageIndicator pageIndicator;

	private TrackScheduleEventAdapter adapter;

	private List<Event> events;

	private EventManager eventManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		events = new ArrayList<Event>();
		setupServices();

		setContentView(R.layout.track_schedule_event);

		Bundle extras = getIntent().getExtras();
		track = (Track) extras.getSerializable(EXTRA_TRACK);

		progress = findViewById(R.id.progress);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new TrackScheduleEventAdapter(getSupportFragmentManager());
		pageIndicator = (PageIndicator) findViewById(R.id.indicator);

		if(savedInstanceState == null) {
			initialPosition = extras.getInt(EXTRA_POSITION, -1);
			pager.setAdapter(adapter);
			pageIndicator.setViewPager(pager);
		}

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.event_details);
		bar.setSubtitle(track.getTitle());

		// Enable Android Beam
		NfcUtils.setAppDataPushMessageCallbackIfAvailable(this, this);

		setCustomProgressVisibility(true);
		getSupportLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
	}

	private void setupServices()
	{
		try {
			eventManager = DatabaseManagerFactory.getEventManager(this);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void setCustomProgressVisibility(boolean isVisible)
	{
		progress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	@Override
	public byte[] createNfcAppData()
	{
		if(adapter.getCount() == 0) {
			return null;
		}

		long eventId = adapter.getItemId(pager.getCurrentItem());
		if(eventId == -1L) {
			return null;
		}

		return String.valueOf(eventId).getBytes();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return false;
	}

	@Override
	public Loader<List<Event>> onCreateLoader(int id, Bundle args)
	{
		return new TrackScheduleLoader(this, track);
	}

	@Override
	public void onLoadFinished(Loader<List<Event>> loader, List<Event> data)
	{
		setCustomProgressVisibility(false);

		if(data != null) {
			events.clear();
			events.addAll(data);
			adapter.notifyDataSetChanged();

			// Delay setting the adapter when the instance state is restored
			// to ensure the current position is restored properly
			if(pager.getAdapter() == null) {
				pager.setAdapter(adapter);
				pageIndicator.setViewPager(pager);
			}

			if(initialPosition != -1) {
				pager.setCurrentItem(initialPosition, false);
				initialPosition = -1;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Event>> loader)
	{
		events.clear();
		adapter.notifyDataSetChanged();
	}

	public class TrackScheduleEventAdapter extends FragmentStatePagerAdapter
	{
		public TrackScheduleEventAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{
			if(CollectionUtils.isEmpty(events)) {
				return 0;
			}

			return events.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			if(CollectionUtils.isEmpty(events)) {
				return null;
			}

			return EventDetailsFragment.newInstance(events.get(position));
		}

		public long getItemId(int id)
		{
			return id;
		}
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
}
