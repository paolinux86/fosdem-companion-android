package it.gulch.linuxday.android.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import it.gulch.linuxday.android.constants.ActionConstants;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 17/09/14.
 */
public class DaysLoader extends GlobalCacheLoader<List<Day>>
{
	private DayManager dayManager;

	private final BroadcastReceiver scheduleRefreshedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			onContentChanged();
		}
	};

	public DaysLoader(Context context, DayManager dayManager)
	{
		super(context);
		// Reload days list when the schedule has been refreshed
		LocalBroadcastManager.getInstance(context).registerReceiver(scheduleRefreshedReceiver, new IntentFilter(
				ActionConstants.ACTION_SCHEDULE_REFRESHED));
		this.dayManager = dayManager;
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(scheduleRefreshedReceiver);
	}

	@Override
	protected List<Day> getCachedResult()
	{
		return dayManager.getCachedDays();
	}

	@Override
	public List<Day> loadInBackground()
	{
		return dayManager.getAll();
	}
}