package it.gulch.linuxday.android.services;

import android.content.Context;

import java.util.Date;

/**
 * Created by paolo on 15/09/14.
 */
public interface PreferencesService
{
	void updateLastUpdateTime(Context context);

	void resetLastUpdateTime(Context context);

	Long getLastUpdateTime(Context context);
}
