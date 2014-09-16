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
package it.gulch.linuxday.android.providers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Simple content provider responsible for search suggestions.
 *
 * @author Christophe Beyls
 */
public class SearchSuggestionProvider extends ContentProvider
{
	private static final int MIN_QUERY_LENGTH = 3;

	private static final int DEFAULT_MAX_RESULTS = 5;

	@Override
	public boolean onCreate()
	{
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri)
	{
		return SearchManager.SUGGEST_MIME_TYPE;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		String query = uri.getLastPathSegment();
		// Ignore empty or too small queries
		if(query == null) {
			return null;
		}
		query = query.trim();
		if((query.length() < MIN_QUERY_LENGTH) || "search_suggest_query".equals(query)) {
			return null;
		}

		String limitParam = uri.getQueryParameter("limit");
		int limit = TextUtils.isEmpty(limitParam) ? DEFAULT_MAX_RESULTS : Integer.parseInt(limitParam);

		try {
			Cursor cursor = getSearchSuggestionResults(query, limit);
			cursor.setNotificationUri(this.getContext().getContentResolver(), uri);

			return cursor;
		} catch(SQLException e) {
			return null;
		}
	}

	public Cursor getSearchSuggestionResults(String query, int limit) throws SQLException
	{
		OrmLiteDatabaseHelper helper = OpenHelperManager.getHelper(getContext(), OrmLiteDatabaseHelper.class);
		Dao<Event, Long> eventDao = helper.getDao(Event.class);

		CloseableIterator<Event> iterator = null;
		Cursor cursor = null;
		try {
			iterator = eventDao.iterator(prepareQuery(eventDao, query, limit));
			AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
			cursor = results.getRawCursor();
		} catch(SQLException e) {
			// FIXME
			e.printStackTrace();
		}

		return cursor;
	}

	private PreparedQuery<Event> prepareQuery(Dao<Event, Long> eventDao, String query, int limit) throws SQLException
	{
		// TODO implementare
		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		return queryBuilder.prepare();
	}
}
