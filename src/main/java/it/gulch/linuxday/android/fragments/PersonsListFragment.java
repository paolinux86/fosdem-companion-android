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
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.PersonInfoActivity;
import it.gulch.linuxday.android.adapters.PeopleAdapter;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.PeopleLoader;
import it.gulch.linuxday.android.model.db.Person;

public class PersonsListFragment extends ListFragment
{
	private static final int PERSONS_LOADER_ID = 1;

	private static final String TAG = PersonsListFragment.class.getSimpleName();

	private PeopleAdapter adapter;

	private List<Person> people;

	private PersonManager personManager;

	private LoaderCallbacks<List<Person>> loaderCallbacks;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		people = new ArrayList<Person>();

		adapter = new PeopleAdapter(getActivity(), people);
		setListAdapter(adapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getListView().setFastScrollEnabled(true);
		setEmptyText(getString(R.string.no_data));
		setListShown(false);

		setupLoaderCallbacks();
	}

	private void setupLoaderCallbacks()
	{
		loaderCallbacks = new LoaderCallbacks<List<Person>>()
		{
			@Override
			public Loader<List<Person>> onCreateLoader(int i, Bundle bundle)
			{
				return new PeopleLoader(getActivity(), personManager);
			}

			@Override
			public void onLoadFinished(Loader<List<Person>> listLoader, List<Person> data)
			{
				if(data != null) {
					people.clear();
					people.addAll(data);
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
			public void onLoaderReset(Loader<List<Person>> listLoader)
			{
				people.clear();
				adapter.notifyDataSetChanged();
			}
		};
		getLoaderManager().initLoader(PERSONS_LOADER_ID, null, loaderCallbacks);
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
			personManager = DatabaseManagerFactory.getPersonManager(activity);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Person person = adapter.getItem(position);
		Intent intent =
				new Intent(getActivity(), PersonInfoActivity.class).putExtra(PersonInfoActivity.EXTRA_PERSON, person);
		startActivity(intent);
	}
}
