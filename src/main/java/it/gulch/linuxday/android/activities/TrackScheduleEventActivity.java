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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.viewpagerindicator.PageIndicator;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.fragments.EventDetailsFragment;
import it.gulch.linuxday.android.loaders.TrackScheduleLoader;
import it.gulch.linuxday.android.model.Day;
import it.gulch.linuxday.android.model.Track;

/**
 * Event view of the track schedule; allows to slide between events of the same track using a ViewPager.
 *
 * @author Christophe Beyls
 */
public class TrackScheduleEventActivity extends ActionBarActivity implements LoaderCallbacks<Cursor>
{
	public static final String EXTRA_DAY = "day";

	public static final String EXTRA_TRACK = "track";

	public static final String EXTRA_POSITION = "position";

	private static final int EVENTS_LOADER_ID = 1;

	private Day day;

	private Track track;

	private int initialPosition = -1;

	private View progress;

	private ViewPager pager;

	private PageIndicator pageIndicator;

	private TrackScheduleEventAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.track_schedule_event);

		Bundle extras = getIntent().getExtras();
		day = extras.getParcelable(EXTRA_DAY);
		track = extras.getParcelable(EXTRA_TRACK);

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
		bar.setSubtitle(track.getName());

		setCustomProgressVisibility(true);
		getSupportLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
	}

	private void setCustomProgressVisibility(boolean isVisible)
	{
		progress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		return new TrackScheduleLoader(this, day, track);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		setCustomProgressVisibility(false);

		if(data != null) {
			adapter.setCursor(data);

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
	public void onLoaderReset(Loader<Cursor> loader)
	{
		adapter.setCursor(null);
	}

	public static class TrackScheduleEventAdapter extends FragmentStatePagerAdapter
	{

		private Cursor cursor;

		public TrackScheduleEventAdapter(FragmentManager fm)
		{
			super(fm);
		}

		public Cursor getCursor()
		{
			return cursor;
		}

		public void setCursor(Cursor cursor)
		{
			this.cursor = cursor;
			notifyDataSetChanged();
		}

		@Override
		public int getCount()
		{
			return (cursor == null) ? 0 : cursor.getCount();
		}

		@Override
		public Fragment getItem(int position)
		{
			cursor.moveToPosition(position);
			return EventDetailsFragment.newInstance(DatabaseManager.toEvent(cursor));
		}
	}
}
