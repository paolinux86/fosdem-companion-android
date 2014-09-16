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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.fragments.EventDetailsFragment;
import it.gulch.linuxday.android.loaders.LocalCacheLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.utils.NfcUtils;

/**
 * Displays a single event passed either as a complete Parcelable object in extras or as an id in data.
 *
 * @author Christophe Beyls
 */
@EActivity
public class EventDetailsActivity extends ActionBarActivity
	implements LoaderCallbacks<Event>, NfcUtils.CreateNfcAppDataCallback
{
	public static final String EXTRA_EVENT = "event";

	private static final int EVENT_LOADER_ID = 1;

	private Event event;

	@Bean(EventManagerImpl.class)
	EventManager eventManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content);

		getSupportActionBar().setTitle(R.string.event_details);

		Event event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);
		if(event != null) {
			// The event has been passed as parameter, it can be displayed immediately
			initEvent(event);
			if(savedInstanceState == null) {
				Fragment f = EventDetailsFragment.newInstance(event);
				getSupportFragmentManager().beginTransaction().add(R.id.content, f).commit();
			}
		} else {
			// Load the event from the DB using its id
			getSupportLoaderManager().initLoader(EVENT_LOADER_ID, null, this);
		}
	}

	/**
	 * Initialize event-related configuration after the event has been loaded.
	 */
	private void initEvent(Event event)
	{
		this.event = event;
		// Enable up navigation only after getting the event details
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Enable Android Beam
		NfcUtils.setAppDataPushMessageCallbackIfAvailable(this, this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case android.R.id.home:
				// Navigate up to the track associated to this event
				Intent upIntent = new Intent(this, TrackScheduleActivity.class);
				upIntent.putExtra(TrackScheduleActivity.EXTRA_DAY, event.getTrack().getDay());
				upIntent.putExtra(TrackScheduleActivity.EXTRA_TRACK, event.getTrack());
				upIntent.putExtra(TrackScheduleActivity.EXTRA_FROM_EVENT_ID, event.getId());
				upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				finish();
				if(NavUtils.shouldUpRecreateTask(this, upIntent)) {
					TaskStackBuilder.create(this).addNextIntent(new Intent(this, MainActivity.class))
						.addNextIntent(upIntent).startActivities();
				} else {
					startActivity(upIntent);
				}
				return true;
		}
		return false;
	}

	@Override
	public byte[] createNfcAppData()
	{
		return String.valueOf(event.getId()).getBytes();
	}

	private class EventLoader extends LocalCacheLoader<Event>
	{
		private final long eventId;

		public EventLoader(Context context, long eventId)
		{
			super(context);
			this.eventId = eventId;
		}

		@Override
		public Event loadInBackground()
		{
			return eventManager.get(eventId);
		}
	}

	@Override
	public Loader<Event> onCreateLoader(int id, Bundle args)
	{
		Intent intent = getIntent();
		String eventIdString;
		if(NfcUtils.hasAppData(intent)) {
			// NFC intent
			eventIdString = new String(NfcUtils.extractAppData(intent));
		} else {
			// Normal in-app intent
			eventIdString = intent.getDataString();
		}

		return new EventLoader(this, Long.parseLong(eventIdString));
	}

	@Override
	public void onLoadFinished(Loader<Event> loader, Event data)
	{
		if(data == null) {
			// Event not found, quit
			Toast.makeText(this, getString(R.string.event_not_found_error), Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		event = data;
		initEvent(data);

		FragmentManager fm = getSupportFragmentManager();
		if(fm.findFragmentById(R.id.content) == null) {
			Fragment f = EventDetailsFragment.newInstance(data);
			fm.beginTransaction().add(R.id.content, f).commitAllowingStateLoss();
		}
	}

	@Override
	public void onLoaderReset(Loader<Event> loader)
	{
	}
}
