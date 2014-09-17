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
package it.gulch.linuxday.android.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.EventDetailsActivity;
import it.gulch.linuxday.android.activities.MainActivity;
import it.gulch.linuxday.android.constants.ActionConstants;
import it.gulch.linuxday.android.constants.ExtraConstants;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.impl.BookmarkManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.fragments.SettingsFragment;
import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.receivers.AlarmReceiver;

/**
 * A service to schedule or unschedule alarms in the background, keeping the app responsive.
 *
 * @author Christophe Beyls
 */
public class AlarmIntentService extends IntentService
{
	private static final String TAG = AlarmIntentService.class.getSimpleName();

	public static final String ACTION_UPDATE_ALARMS = "it.gulch.linuxday.android.action.UPDATE_ALARMS";

	public static final String ACTION_DISABLE_ALARMS = "it.gulch.linuxday.android.action.DISABLE_ALARMS";

	public static final String EXTRA_WITH_WAKE_LOCK = "with_wake_lock";

	private AlarmManager alarmManager;

	private BookmarkManager bookmarkManager;

	private EventManager eventManager;

	public AlarmIntentService()
	{
		super("AlarmIntentService");
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		setupServices();

		// Ask for the last unhandled intents to be redelivered if the service dies early.
		// This ensures we handle all events, in order.
		setIntentRedelivery(true);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	private void setupServices()
	{
		try {
			eventManager = DatabaseManagerFactory.getEventManager(this);
			bookmarkManager = DatabaseManagerFactory.getBookmarkManager(this);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private PendingIntent getAlarmPendingIntent(long eventId)
	{
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.setAction(AlarmReceiver.ACTION_NOTIFY_EVENT);
		intent.setData(Uri.parse(String.valueOf(eventId)));

		return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String action = intent.getAction();

		if(ACTION_UPDATE_ALARMS.equals(action)) {
			updateAlarms(intent);
		} else if(ACTION_DISABLE_ALARMS.equals(action)) {
			disableAlarms();
		} else if(ActionConstants.ACTION_ADD_BOOKMARK.equals(action)) {
			addBookmark(intent);
		} else if(ActionConstants.ACTION_REMOVE_BOOKMARKS.equals(action)) {
			removeBookmarks(intent);
		} else if(AlarmReceiver.ACTION_NOTIFY_EVENT.equals(action)) {
			notifyEvent(intent);
			AlarmReceiver.completeWakefulIntent(intent);
		}

		// TODO: ELSE?
	}

	private void updateAlarms(Intent intent)
	{
		// Create/update all alarms
		long delay = getDelay();
		long now = System.currentTimeMillis();

		List<Bookmark> bookmarks = bookmarkManager.getBookmarks(now);
		for(Bookmark bookmark : bookmarks) {
			Event event = bookmark.getEvent();
			long notificationTime = event.getStartDate().getTime() - delay;

			PendingIntent pendingIntent = getAlarmPendingIntent(event.getId());
			if(notificationTime < now) {
				// Cancel pending alarms that where scheduled between now and delay, if any
				alarmManager.cancel(pendingIntent);
			} else {
				alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
			}
		}

		// Release the wake lock setup by AlarmReceiver, if any
		if(intent.getBooleanExtra(EXTRA_WITH_WAKE_LOCK, false)) {
			AlarmReceiver.completeWakefulIntent(intent);
		}
	}

	private void disableAlarms()
	{
		// Cancel alarms of every bookmark in the future
		List<Bookmark> bookmarks = bookmarkManager.getBookmarks(System.currentTimeMillis());
		for(Bookmark bookmark : bookmarks) {
			long eventId = bookmark.getEvent().getId();
			alarmManager.cancel(getAlarmPendingIntent(eventId));
		}
	}

	private void addBookmark(Intent intent)
	{
		long delay = getDelay();
		long eventId = intent.getLongExtra(ExtraConstants.EXTRA_EVENT_ID, -1L);
		long startTime = intent.getLongExtra(ExtraConstants.EXTRA_EVENT_START_TIME, -1L);

		// Only schedule future events. If they start before the delay, the alarm will go off immediately
		if((startTime == -1L) || (startTime < System.currentTimeMillis())) {
			return;
		}

		alarmManager.set(AlarmManager.RTC_WAKEUP, startTime - delay, getAlarmPendingIntent(eventId));
	}

	private void removeBookmarks(Intent intent)
	{
		// Cancel matching alarms, might they exist or not
		long[] eventIds = intent.getLongArrayExtra(ExtraConstants.EXTRA_EVENT_IDS);
		for(long eventId : eventIds) {
			alarmManager.cancel(getAlarmPendingIntent(eventId));
		}
	}

	private void notifyEvent(Intent intent)
	{
		long eventId = Long.parseLong(intent.getDataString());
		Event event = eventManager.get(eventId);
		if(event == null) {
			return;
		}

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent eventPendingIntent =
				TaskStackBuilder.create(this).addNextIntent(new Intent(this, MainActivity.class)).addNextIntent(
						new Intent(this, EventDetailsActivity.class).setData(Uri.parse(String.valueOf(event.getId()))))
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		int defaultFlags = Notification.DEFAULT_SOUND;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS_VIBRATE, false)) {
			defaultFlags |= Notification.DEFAULT_VIBRATE;
		}
		if(sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS_LED, false)) {
			defaultFlags |= Notification.DEFAULT_LIGHTS;
		}

		String trackName = event.getTrack().getTitle();
		CharSequence bigText;
		String contentText;
		if(CollectionUtils.isEmpty(event.getPeople())) {
			contentText = trackName;
			bigText = event.getSubtitle();
		} else {
			String personsSummary = StringUtils.join(event.getPeople(), ", ");
			contentText = String.format("%1$s - %2$s", trackName, personsSummary);
			String subTitle = event.getSubtitle();
			if(TextUtils.isEmpty(subTitle)) {
				bigText = personsSummary;
			} else {
				SpannableString spannableBigText =
						new SpannableString(String.format("%1$s\n%2$s", subTitle, personsSummary));
				// Set the subtitle in white color
				spannableBigText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, subTitle.length(),
										 Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				bigText = spannableBigText;
			}
		}

		String roomName = event.getTrack().getRoom().getName();
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
						.setWhen(event.getStartDate().getTime()).setContentTitle(event.getTitle())
						.setContentText(contentText)
						.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText).setSummaryText(trackName))
						.setContentInfo(roomName).setContentIntent(eventPendingIntent).setAutoCancel(true)
						.setDefaults(defaultFlags).setPriority(NotificationCompat.PRIORITY_DEFAULT);

		// Add an optional action button to show the room map image
		//		int roomImageResId = getResources()
		//				.getIdentifier(StringUtils.roomNameToResourceName(roomName), "drawable", getPackageName());
		//		if(roomImageResId != 0) {
		//			// The room name is the unique Id of a RoomImageDialogActivity
		//			Intent mapIntent = new Intent(this, RoomImageDialogActivity.class).setFlags(Intent
		// .FLAG_ACTIVITY_NEW_TASK)
		//					.setData(Uri.parse(roomName));
		//			mapIntent.putExtra(RoomImageDialogActivity.EXTRA_ROOM_NAME, roomName);
		//			mapIntent.putExtra(RoomImageDialogActivity.EXTRA_ROOM_IMAGE_RESOURCE_ID, roomImageResId);
		//			PendingIntent mapPendingIntent =
		//					PendingIntent.getActivity(this, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//			notificationBuilder.addAction(R.drawable.ic_action_place, getString(R.string.room_map),
		// mapPendingIntent);
		//		}

		notificationManager.notify((int) eventId, notificationBuilder.build());
	}

	private long getDelay()
	{
		String delayString = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(SettingsFragment.KEY_PREF_NOTIFICATIONS_DELAY, "0");
		// Convert from minutes to milliseconds
		return Long.parseLong(delayString) * 1000L * 60L;
	}
}
