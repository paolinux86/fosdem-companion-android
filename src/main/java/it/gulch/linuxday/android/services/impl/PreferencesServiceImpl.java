package it.gulch.linuxday.android.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import it.gulch.linuxday.android.constants.SharedPreferencesConstants;
import it.gulch.linuxday.android.services.PreferencesService;

/**
 * Created by paolo on 15/09/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class PreferencesServiceImpl implements PreferencesService
{
	@RootContext
	Context context;

	@Override
	public void updateLastUpdateTime()
	{
		SharedPreferences sharedPreferences = getSharedPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF, System.currentTimeMillis());
		editor.commit();
	}

	@Override
	public void resetLastUpdateTime()
	{
		getSharedPreferences().edit().remove(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF).commit();
	}

	@Override
	public Long getLastUpdateTime()
	{
		return getSharedPreferences().getLong(SharedPreferencesConstants.LAST_UPDATE_TIME_PREF, 0);
	}

	private SharedPreferences getSharedPreferences()
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
