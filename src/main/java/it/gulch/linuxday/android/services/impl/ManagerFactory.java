package it.gulch.linuxday.android.services.impl;

import it.gulch.linuxday.android.services.PreferencesService;

/**
 * Created by paolo on 17/09/14.
 */
public class ManagerFactory
{
	private static PreferencesService preferencesService;

	private ManagerFactory()
	{
	}

	public static PreferencesService getPreferencesService()
	{
		if(preferencesService == null) {
			preferencesService = new PreferencesServiceImpl();
		}

		return preferencesService;
	}
}
