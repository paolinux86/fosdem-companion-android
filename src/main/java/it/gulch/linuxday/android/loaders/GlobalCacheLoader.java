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

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * A Loader working with a global application cache instead of a local cache.
 * This allows to avoid starting a background thread if the result is already in cache.
 * You do NOT need to destroy this loader after the result has been delivered.
 * The cache will be checked each time the fragment is started.
 *
 * @author Christophe Beyls
 */
public abstract class GlobalCacheLoader<T> extends AsyncTaskLoader<T>
{
	public GlobalCacheLoader(Context context)
	{
		super(context);
	}

	@Override
	protected void onStartLoading()
	{
		T cachedResult = getCachedResult();
		if(cachedResult != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(cachedResult);
		}

		if(takeContentChanged() || cachedResult == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
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
	}

	@Override
	public void deliverResult(T data)
	{
		if(isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}
	}

	protected abstract T getCachedResult();
}
