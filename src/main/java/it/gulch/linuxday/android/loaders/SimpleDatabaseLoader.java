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
 * A CursorLoader that doesn't need a ContentProvider.
 *
 * @author Christophe Beyls
 */
public abstract class SimpleDatabaseLoader<T> extends AsyncTaskLoader<T>
{
	private final ForceLoadContentObserver mObserver;

	private T mObject;

	public SimpleDatabaseLoader(Context context)
	{
		super(context);
		mObserver = new ForceLoadContentObserver();
	}

	/* Runs on a worker thread */
	@Override
	public T loadInBackground()
	{
		return getObject();
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(T object)
	{
		if(isReset()) {
			return;
		}

		mObject = object;
		if(isStarted()) {
			super.deliverResult(object);
		}
	}

	/**
	 * Starts an asynchronous load of the data. When the result is ready the callbacks will be called on the UI thread. If a previous load has been completed
	 * and is still valid the result may be passed to the callbacks immediately.
	 * <p/>
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading()
	{
		if(mObject != null) {
			deliverResult(mObject);
		}
		if(takeContentChanged() || mObject == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading()
	{
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(T object)
	{
		// Retry a refresh the next time the loader is started
		onContentChanged();
	}

	@Override
	protected void onReset()
	{
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		mObject = null;
	}

	protected abstract T getObject();
}
