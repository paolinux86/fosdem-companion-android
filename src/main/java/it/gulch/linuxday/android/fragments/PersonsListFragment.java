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

import java.util.ArrayList;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.PersonInfoActivity;
import it.gulch.linuxday.android.adapters.PeopleAdapter;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.db.manager.impl.PersonManagerImpl;
import it.gulch.linuxday.android.loaders.SimpleDatabaseLoader;
import it.gulch.linuxday.android.model.db.Person;

@EFragment
public class PersonsListFragment extends ListFragment implements LoaderCallbacks<List<Person>>
{
	private static final int PERSONS_LOADER_ID = 1;

	private PeopleAdapter adapter;

	private List<Person> people;

	@Bean(PersonManagerImpl.class)
	PersonManager personManager;

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

		getLoaderManager().initLoader(PERSONS_LOADER_ID, null, this);
	}

	private class PersonsLoader extends SimpleDatabaseLoader<List<Person>>
	{
		public PersonsLoader(Context context)
		{
			super(context);
		}

		@Override
		protected List<Person> getObject()
		{
			return personManager.getAll();
		}
	}

	@Override
	public Loader<List<Person>> onCreateLoader(int id, Bundle args)
	{
		return new PersonsLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Person>> loader, List<Person> data)
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
	public void onLoaderReset(Loader<List<Person>> loader)
	{
		people.clear();
		adapter.notifyDataSetChanged();
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
