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
package it.gulch.linuxday.android.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import it.gulch.linuxday.android.alarms.FosdemAlarmManager;
import it.gulch.linuxday.android.services.AlarmIntentService;

/**
 * Entry point for system-generated events: boot complete and alarms.
 *
 * @author Christophe Beyls
 */
public class AlarmReceiver extends WakefulBroadcastReceiver
{
	public static final String ACTION_NOTIFY_EVENT = "it.gulch.linuxday.android.action.NOTIFY_EVENT";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();

		if(ACTION_NOTIFY_EVENT.equals(action)) {

			// Forward the intent to the AlarmIntentService for background processing of the notification
			Intent serviceIntent = new Intent(context, AlarmIntentService.class);
			serviceIntent.setAction(ACTION_NOTIFY_EVENT);
			serviceIntent.setData(intent.getData());
			startWakefulService(context, serviceIntent);

		} else if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {

			if(FosdemAlarmManager.getInstance().isEnabled()) {
				Intent serviceIntent = new Intent(context, AlarmIntentService.class);
				serviceIntent.setAction(AlarmIntentService.ACTION_UPDATE_ALARMS);
				serviceIntent.putExtra(AlarmIntentService.EXTRA_WITH_WAKE_LOCK, true);
				startWakefulService(context, serviceIntent);
			}
		}
	}

}
