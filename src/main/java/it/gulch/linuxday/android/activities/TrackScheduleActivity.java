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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.fragments.EventDetailsFragment;
import it.gulch.linuxday.android.fragments.RoomImageDialogFragment;
import it.gulch.linuxday.android.fragments.TrackScheduleListFragment;
import it.gulch.linuxday.android.model.Day;
import it.gulch.linuxday.android.model.Event;
import it.gulch.linuxday.android.model.Track;

/**
 * Track Schedule container, works in both single pane and dual pane modes.
 *
 * @author Christophe Beyls
 */
public class TrackScheduleActivity extends ActionBarActivity implements TrackScheduleListFragment.Callbacks
{
	public static final String EXTRA_DAY = "day";

	public static final String EXTRA_TRACK = "track";

	// Optional extra used as a hint for up navigation from an event
	public static final String EXTRA_FROM_EVENT_ID = "from_event_id";

	private Day day;

	private Track track;

	private boolean isTabletLandscape;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_schedule);

		Bundle extras = getIntent().getExtras();
		day = extras.getParcelable(EXTRA_DAY);
		track = extras.getParcelable(EXTRA_TRACK);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(track.toString());
		bar.setSubtitle(day.toString());

		isTabletLandscape = getResources().getBoolean(R.bool.tablet_landscape);

		TrackScheduleListFragment trackScheduleListFragment;
		FragmentManager fm = getSupportFragmentManager();
		if(savedInstanceState == null) {
			long fromEventId = extras.getLong(EXTRA_FROM_EVENT_ID, -1L);
			if(fromEventId != -1L) {
				trackScheduleListFragment = TrackScheduleListFragment.newInstance(day, track, fromEventId);
			} else {
				trackScheduleListFragment = TrackScheduleListFragment.newInstance(day, track);
			}

			fm.beginTransaction().add(R.id.schedule, trackScheduleListFragment).commit();
		} else {
			trackScheduleListFragment = (TrackScheduleListFragment) fm.findFragmentById(R.id.schedule);

			// Remove the room image DialogFragment when switching from dual pane to single pane mode
			if(!isTabletLandscape) {
				Fragment roomImageDialogFragment = fm.findFragmentByTag(RoomImageDialogFragment.TAG);
				if(roomImageDialogFragment != null) {
					fm.beginTransaction().remove(roomImageDialogFragment).commit();
				}
			}
		}
		trackScheduleListFragment.setSelectionEnabled(isTabletLandscape);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return false;
	}

	@Override
	public void onEventSelected(int position, Event event)
	{
		if(isTabletLandscape) {
			// Tablet mode: Show event details in the right pane fragment
			FragmentManager fm = getSupportFragmentManager();
			EventDetailsFragment currentFragment = (EventDetailsFragment) fm.findFragmentById(R.id.event);
			if(event != null) {
				// Only replace the fragment if the event is different
				if((currentFragment == null) || !currentFragment.getEvent().equals(event)) {
					Fragment f = EventDetailsFragment.newInstance(event);
					// Allow state loss since the event fragment will be synchronized with the list selection after activity re-creation
					fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.replace(R.id.event, f).commitAllowingStateLoss();
				}
			} else {
				// Nothing is selected because the list is empty
				if(currentFragment != null) {
					fm.beginTransaction().remove(currentFragment).commitAllowingStateLoss();
				}
			}
		} else {
			// Classic mode: Show event details in a new activity
			Intent intent = new Intent(this, TrackScheduleEventActivity.class);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_DAY, day);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_TRACK, track);
			intent.putExtra(TrackScheduleEventActivity.EXTRA_POSITION, position);
			startActivity(intent);
		}
	}
}
