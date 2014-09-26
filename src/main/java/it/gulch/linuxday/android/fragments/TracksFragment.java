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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.common.view.SlidingTabLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.adapters.DaysAdapter;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.DaysLoader;
import it.gulch.linuxday.android.model.db.Day;

public class TracksFragment extends Fragment
{
	private static final String TAG = TracksFragment.class.getSimpleName();

	private static class ViewHolder
	{
		View contentView;

		View emptyView;

		ViewPager pager;

		SlidingTabLayout slidingTabs;
	}

	private static final int DAYS_LOADER_ID = 1;

	private static final String PREF_CURRENT_PAGE = "tracks_current_page";

	private DaysAdapter daysAdapter;

	private ViewHolder holder;

	private int savedCurrentPage = -1;

	private LoaderCallbacks<List<Day>> loaderCallbacks;

	private DayManager dayManager;

	private List<Day> days;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		days = new ArrayList<Day>();
		daysAdapter = new DaysAdapter(getChildFragmentManager(), days);

		if(savedInstanceState == null) {
			// Restore the current page from preferences
			savedCurrentPage = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(PREF_CURRENT_PAGE, -1);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_tracks, container, false);

		holder = new ViewHolder();
		holder.contentView = view.findViewById(R.id.content);
		holder.emptyView = view.findViewById(android.R.id.empty);
		holder.pager = (ViewPager) view.findViewById(R.id.pager);
		holder.slidingTabs = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
		holder.slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.maincolor));

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		holder = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		loaderCallbacks = new LoaderCallbacks<List<Day>>()
		{
			@Override
			public Loader<List<Day>> onCreateLoader(int i, Bundle bundle)
			{
				return new DaysLoader(getActivity(), dayManager);
			}

			@Override
			public void onLoadFinished(Loader<List<Day>> listLoader, List<Day> data)
			{
				days.clear();
				days.addAll(data);
				daysAdapter.notifyDataSetChanged();

				final int totalPages = daysAdapter.getCount();
				if(totalPages == 0) {
					holder.contentView.setVisibility(View.GONE);
					holder.emptyView.setVisibility(View.VISIBLE);
					holder.pager.setOnPageChangeListener(null);
				} else {
					holder.contentView.setVisibility(View.VISIBLE);
					holder.emptyView.setVisibility(View.GONE);
					if(holder.pager.getAdapter() == null) {
						holder.pager.setAdapter(daysAdapter);
					}
					holder.slidingTabs.setViewPager(holder.pager);
					if(savedCurrentPage != -1) {
						holder.pager.setCurrentItem(Math.min(savedCurrentPage, totalPages - 1), false);
						savedCurrentPage = -1;
					}
				}
			}

			@Override
			public void onLoaderReset(Loader<List<Day>> listLoader)
			{
				days.clear();
				daysAdapter.notifyDataSetChanged();
			}
		};

		getLoaderManager().restartLoader(DAYS_LOADER_ID, null, loaderCallbacks);
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
			dayManager = DatabaseManagerFactory.getDayManager(activity);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onDestroy()
	{
		getLoaderManager().destroyLoader(DAYS_LOADER_ID);
		super.onDestroy();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		// Save the current page to preferences if it has changed
		final int page = holder.pager.getCurrentItem();
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		if(prefs.getInt(PREF_CURRENT_PAGE, -1) != page) {
			prefs.edit().putInt(PREF_CURRENT_PAGE, page).commit();
		}
	}
}
