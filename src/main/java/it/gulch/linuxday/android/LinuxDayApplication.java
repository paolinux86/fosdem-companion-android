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
package it.gulch.linuxday.android;

import android.app.Application;
import android.preference.PreferenceManager;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import it.gulch.linuxday.android.alarms.FosdemAlarmManager;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.PACKAGE_NAME;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(formKey = "",
				//formUri = "http://acra.slack-counter.org/acra-ldca/_design/acra-storage/_update/report",
				formUri = "http://91.121.146.95:5984/acra-ldca/_design/acra-storage/_update/report",
				reportType = org.acra.sender.HttpSender.Type.JSON,
				httpMethod = org.acra.sender.HttpSender.Method.PUT,
				formUriBasicAuthLogin="ldca",
				formUriBasicAuthPassword="4lM0te_n,A5",
				customReportContent = { REPORT_ID, APP_VERSION_CODE, APP_VERSION_NAME, PHONE_MODEL, BRAND, PRODUCT,
						ANDROID_VERSION, STACK_TRACE, PACKAGE_NAME })
public class LinuxDayApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		// Initialize settings
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		// Alarms (requires settings)
		FosdemAlarmManager.init(this);

		ACRA.init(this);
	}
}
