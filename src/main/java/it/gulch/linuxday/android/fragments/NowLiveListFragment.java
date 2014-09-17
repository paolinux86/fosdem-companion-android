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
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.loaders.BaseLiveLoader;
import it.gulch.linuxday.android.model.db.Event;

public class NowLiveListFragment extends BaseLiveListFragment
{
	private static final String TAG = NextLiveListFragment.class.getSimpleName();

	private EventManager eventManager;

	@Override
	protected String getEmptyText()
	{
		return getString(R.string.now_empty);
	}

	@Override
	public Loader<List<Event>> onCreateLoader(int id, Bundle args)
	{
		return new NowLiveLoader(getActivity());
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
			eventManager = DatabaseManagerFactory.getEventManager(activity);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private class NowLiveLoader extends BaseLiveLoader
	{
		public NowLiveLoader(Context context)
		{
			super(context);
		}

		@Override
		protected List<Event> getObject()
		{
			Date now = GregorianCalendar.getInstance().getTime();

			try {
				return eventManager.search(null, now, now, DatabaseOrder.DESCENDING);
			} catch(SQLException e) {
				return Collections.emptyList();
			}
		}
	}
}
