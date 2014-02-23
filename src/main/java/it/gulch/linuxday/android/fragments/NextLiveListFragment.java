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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.loaders.BaseLiveLoader;

public class NextLiveListFragment extends BaseLiveListFragment
{
	@Override
	protected String getEmptyText()
	{
		return getString(R.string.next_empty);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		return new NextLiveLoader(getActivity());
	}

	private static class NextLiveLoader extends BaseLiveLoader
	{
		private static final long INTERVAL = 30L * 60L * 1000L; // 30 minutes

		public NextLiveLoader(Context context)
		{
			super(context);
		}

		@Override
		protected Cursor getCursor()
		{
			long now = System.currentTimeMillis();
			return DatabaseManager.getInstance().getEvents(now, now + INTERVAL, -1L, true);
		}
	}
}
