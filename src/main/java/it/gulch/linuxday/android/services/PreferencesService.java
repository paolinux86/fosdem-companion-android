package it.gulch.linuxday.android.services;

import java.util.Date;

/**
 * Created by paolo on 15/09/14.
 */
public interface PreferencesService
{
	void updateLastUpdateTime();

	void resetLastUpdateTime();

	Long getLastUpdateTime();
}
