package it.gulch.linuxday.android;

import android.app.Application;
import android.preference.PreferenceManager;
import it.gulch.linuxday.android.alarms.FosdemAlarmManager;
import it.gulch.linuxday.android.db.DatabaseManager;

public class FosdemApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseManager.init(this);
		// Initialize settings
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		// Alarms (requires settings)
		FosdemAlarmManager.init(this);
	}
}
