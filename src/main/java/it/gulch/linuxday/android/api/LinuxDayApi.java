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
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.gulch.linuxday.android.db.ConferenceImportManager;
import it.gulch.linuxday.android.model.json.Conference;
import it.gulch.linuxday.android.utils.HttpUtils;

/**
 * Main API entry point.
 *
 * @author Christophe Beyls
 * @author paolo
 */
@EBean
public class LinuxDayApi
{
	private static final String TAG = LinuxDayApi.class.getSimpleName();

	// Local broadcasts parameters
	public static final String ACTION_DOWNLOAD_SCHEDULE_PROGRESS =
			"it.gulch.linuxday.android.action.DOWNLOAD_SCHEDULE_PROGRESS";

	public static final String EXTRA_PROGRESS = "PROGRESS";

	public static final String ACTION_DOWNLOAD_SCHEDULE_RESULT =
			"it.gulch.linuxday.android.action.DOWNLOAD_SCHEDULE_RESULT";

	public static final String EXTRA_RESULT = "RESULT";

	public static final int RESULT_ERROR = -1;

	private static final Lock scheduleLock = new ReentrantLock();

	@Bean
	ConferenceImportManager conferenceImportManager;

	/**
	 * Download & store the schedule to the database. Only one thread at a time will perform the actual action,
	 * the other ones will return immediately. The
	 * result will be sent back in the form of a local broadcast with an ACTION_DOWNLOAD_SCHEDULE_RESULT action.
	 */
	public void downloadSchedule(Context context)
	{
		if(!scheduleLock.tryLock()) {
			// If a download is already in progress, return immediately
			return;
		}

		InputStream inputStream = doDownload(context);
		//int result = parseXml(inputStream);
		long result = parseJson(inputStream);
		sendResult(context, result);
	}

	private InputStream doDownload(Context context)
	{
		InputStream inputStream;
		try {
			String scheduleUrl = LinuxDayUrls.getSchedule();
			inputStream = HttpUtils.get(context, scheduleUrl, ACTION_DOWNLOAD_SCHEDULE_PROGRESS, EXTRA_PROGRESS);
		} catch(IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}

		return inputStream;
	}

	private long parseJson(InputStream inputStream)
	{
		long result = RESULT_ERROR;

		try {
			Conference conference = new ObjectMapper().readValue(inputStream, Conference.class);
			result = conferenceImportManager.importConference(conference);
			//DatabaseManager.getInstance().storeSchedule(events);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		IOUtils.closeQuietly(inputStream);

		return result;
	}

	private void sendResult(Context context, long result)
	{
		Intent intent = new Intent(ACTION_DOWNLOAD_SCHEDULE_RESULT);
		intent.putExtra(EXTRA_RESULT, result);

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
		broadcastManager.sendBroadcast(intent);

		scheduleLock.unlock();
	}
}
