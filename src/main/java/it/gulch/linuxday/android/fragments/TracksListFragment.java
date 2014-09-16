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
import android.os.Bundle;
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
import it.gulch.linuxday.android.activities.TrackScheduleActivity;
import it.gulch.linuxday.android.adapters.TracksAdapter;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.db.manager.impl.TrackManagerImpl;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Track;

@EFragment
public class TracksListFragment extends ListFragment implements LoaderCallbacks<List<Track>>
{
	private static final int TRACKS_LOADER_ID = 1;

	private static final String ARG_DAY = "day";

	private Day day;

	private TracksAdapter adapter;

	private List<Track> tracks;

	@Bean(TrackManagerImpl.class)
	TrackManager trackManager;

	public static TracksListFragment newInstance(Day day)
	{
		TracksListFragment f = new TracksListFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_DAY, day);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		tracks = new ArrayList<Track>();

		adapter = new TracksAdapter(getActivity(), tracks);
		day = (Day) getArguments().getSerializable(ARG_DAY);
		setListAdapter(adapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.no_data));
		setListShown(false);

		getLoaderManager().initLoader(TRACKS_LOADER_ID, null, this);
	}

	private class TracksLoader extends SimpleDatabaseLoader<List<Track>>
	{
		private final Day day;

		public TracksLoader(Context context, Day day)
		{
			super(context);
			this.day = day;
		}

		@Override
		protected List<Track> getObject()
		{
			try {
				return trackManager.findByDay(day);
			} catch(SQLException e) {
				return Collections.emptyList();
			}
		}
	}

	@Override
	public Loader<List<Track>> onCreateLoader(int id, Bundle args)
	{
		return new TracksLoader(getActivity(), day);
	}

	@Override
	public void onLoadFinished(Loader<List<Track>> loader, List<Track> data)
	{
		if(data != null) {
			tracks.clear();
			tracks.addAll(data);
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
	public void onLoaderReset(Loader<List<Track>> loader)
	{
		tracks.clear();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Track track = adapter.getItem(position);
		Intent intent =
				new Intent(getActivity(), TrackScheduleActivity.class).putExtra(TrackScheduleActivity.EXTRA_DAY, day)
						.putExtra(TrackScheduleActivity.EXTRA_TRACK, track);
		startActivity(intent);
	}
}
