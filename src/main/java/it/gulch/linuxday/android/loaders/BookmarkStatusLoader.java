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
package it.gulch.linuxday.android.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.model.Event;
import it.gulch.linuxday.android.utils.ArrayUtils;

/**
 * This loader retrieves the bookmark status of an event from the database, then updates it in real time by listening to broadcasts.
 *
 * @author Christophe Beyls
 */
public class BookmarkStatusLoader extends AsyncTaskLoader<Boolean>
{
	private final Event event;

	private Boolean isBookmarked;

	private final BroadcastReceiver addBookmarkReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(event.getId() == intent.getLongExtra(DatabaseManager.EXTRA_EVENT_ID, -1L)) {
				updateBookmark(true);
			}
		}
	};

	private final BroadcastReceiver removeBookmarksReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			long[] eventIds = intent.getLongArrayExtra(DatabaseManager.EXTRA_EVENT_IDS);
			if(ArrayUtils.indexOf(eventIds, event.getId()) != -1) {
				updateBookmark(false);
			}
		}
	};

	public BookmarkStatusLoader(Context context, Event event)
	{
		super(context);
		this.event = event;

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
		lbm.registerReceiver(addBookmarkReceiver, new IntentFilter(DatabaseManager.ACTION_ADD_BOOKMARK));
		lbm.registerReceiver(removeBookmarksReceiver, new IntentFilter(DatabaseManager.ACTION_REMOVE_BOOKMARKS));
	}

	private void updateBookmark(Boolean result)
	{
		if(isStarted()) {
			cancelLoad();
		}
		deliverResult(result);
	}

	@Override
	protected void onStartLoading()
	{
		if(isBookmarked != null) {
			// If we currently have a result available, deliver it
			// immediately.
			super.deliverResult(isBookmarked);
		} else {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading()
	{
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	protected void onReset()
	{
		super.onReset();

		onStopLoading();
		isBookmarked = null;

		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
		lbm.unregisterReceiver(addBookmarkReceiver);
		lbm.unregisterReceiver(removeBookmarksReceiver);
	}

	@Override
	public void deliverResult(Boolean data)
	{
		isBookmarked = data;

		if(isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}
	}

	@Override
	public Boolean loadInBackground()
	{
		return DatabaseManager.getInstance().isBookmarked(event);
	}
}
