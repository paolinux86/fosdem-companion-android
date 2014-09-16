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
import android.os.Handler;

import java.util.List;

import it.gulch.linuxday.android.model.db.Event;

/**
 * A cursor loader which also automatically refreshes its data at a specified interval.
 *
 * @author Christophe Beyls
 */
public abstract class BaseLiveLoader extends SimpleDatabaseLoader<List<Event>>
{
	private static final long REFRESH_INTERVAL = 60L * 1000L; // 1 minute

	private final Handler handler;

	private final Runnable timeoutRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			onContentChanged();
		}
	};

	public BaseLiveLoader(Context context)
	{
		super(context);
		this.handler = new Handler();
	}

	@Override
	protected void onForceLoad()
	{
		super.onForceLoad();
		handler.removeCallbacks(timeoutRunnable);
		handler.postDelayed(timeoutRunnable, REFRESH_INTERVAL);
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		handler.removeCallbacks(timeoutRunnable);
	}
}
