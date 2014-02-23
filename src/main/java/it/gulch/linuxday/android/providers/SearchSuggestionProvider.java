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

import it.gulch.linuxday.android.db.DatabaseManager;

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

		return DatabaseManager.getInstance().getSearchSuggestionResults(query, limit);
	}
}
