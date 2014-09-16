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
package it.gulch.linuxday.android.widgets;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;

import java.sql.SQLException;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.EventManager;

/**
 * Context menu for the bookmarks list items, available for API 11+ only.
 *
 * @author Christophe Beyls
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BookmarksMultiChoiceModeListener implements MultiChoiceModeListener
{
	private AbsListView listView;

	private BookmarkManager bookmarkManager;

	public static void register(AbsListView listView, BookmarkManager bookmarkManager)
	{
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		BookmarksMultiChoiceModeListener listener = new BookmarksMultiChoiceModeListener(listView, bookmarkManager);
		listView.setMultiChoiceModeListener(listener);
	}

	private BookmarksMultiChoiceModeListener(AbsListView listView, BookmarkManager bookmarkManager)
	{
		this.listView = listView;
		this.bookmarkManager = bookmarkManager;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		updateSelectedCountDisplay(mode);
		return true;
	}

	private void updateSelectedCountDisplay(ActionMode mode)
	{
		int count = listView.getCheckedItemCount();
		mode.setTitle(listView.getContext().getResources().getQuantityString(R.plurals.selected, count, count));
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		mode.getMenuInflater().inflate(R.menu.action_mode_bookmarks, menu);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.delete:
				// Remove multiple bookmarks at once
				new RemoveBookmarksAsyncTask().execute(listView.getCheckedItemIds());
				mode.finish();
				return true;
		}

		return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
	{
		updateSelectedCountDisplay(mode);
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
	}

	private class RemoveBookmarksAsyncTask extends AsyncTask<long[], Void, Void>
	{
		@Override
		protected Void doInBackground(long[]... params)
		{
			try {
				bookmarkManager.removeBookmarksByEventId(params[0]);
			} catch(SQLException e) {
				// FIXME
				e.printStackTrace();
			}

			return null;
		}
	}
}
