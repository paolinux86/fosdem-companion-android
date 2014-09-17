package it.gulch.linuxday.android.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.gulch.linuxday.android.constants.SharedPreferencesConstants;
import it.gulch.linuxday.android.services.PreferencesService;

/**
 * Created by paolo on 15/09/14.
 */
public class PreferencesServiceImpl implements PreferencesService
{
	PreferencesServiceImpl()
	{
	}

	@Override
	public void updateLastUpdateTime(Context context)
	{
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF, System.currentTimeMillis());
		editor.commit();
	}

	@Override
	public void resetLastUpdateTime(Context context)
	{
		getSharedPreferences(context).edit().remove(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF).commit();
	}

	@Override
	public Long getLastUpdateTime(Context context)
	{
		return getSharedPreferences(context).getLong(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF, 0);
	}

	private SharedPreferences getSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
