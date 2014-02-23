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
package it.gulch.linuxday.android.api;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.model.Event;
import it.gulch.linuxday.android.parsers.EventsParser;
import it.gulch.linuxday.android.utils.HttpUtils;

/**
 * Main API entry point.
 *
 * @author Christophe Beyls
 */
public class FosdemApi
{
	// Local broadcasts parameters
	public static final String ACTION_DOWNLOAD_SCHEDULE_PROGRESS =
		"it.gulch.linuxday.android.action.DOWNLOAD_SCHEDULE_PROGRESS";

	public static final String EXTRA_PROGRESS = "PROGRESS";

	public static final String ACTION_DOWNLOAD_SCHEDULE_RESULT =
		"it.gulch.linuxday.android.action.DOWNLOAD_SCHEDULE_RESULT";

	public static final String EXTRA_RESULT = "RESULT";

	public static final int RESULT_ERROR = -1;

	private static final Lock scheduleLock = new ReentrantLock();

	/**
	 * Download & store the schedule to the database. Only one thread at a time will perform the actual action, the other ones will return immediately. The
	 * result will be sent back in the form of a local broadcast with an ACTION_DOWNLOAD_SCHEDULE_RESULT action.
	 */
	public static void downloadSchedule(Context context)
	{
		if(!scheduleLock.tryLock()) {
			// If a download is already in progress, return immediately
			return;
		}

		int result = RESULT_ERROR;
		try {
			InputStream is =
				HttpUtils.get(context, FosdemUrls.getSchedule(), ACTION_DOWNLOAD_SCHEDULE_PROGRESS, EXTRA_PROGRESS);
			try {
				Iterable<Event> events = new EventsParser().parse(is);
				result = DatabaseManager.getInstance().storeSchedule(events);
			} finally {
				try {
					is.close();
				} catch(Exception e) {
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			LocalBroadcastManager.getInstance(context)
				.sendBroadcast(new Intent(ACTION_DOWNLOAD_SCHEDULE_RESULT).putExtra(EXTRA_RESULT, result));
			scheduleLock.unlock();
		}
	}
}
