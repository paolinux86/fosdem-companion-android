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
package it.gulch.linuxday.android.api;

import java.util.Locale;

/**
 * This class contains all FOSDEM Urls
 *
 * @author Christophe Beyls
 */
public class LinuxDayUrls
{
	private LinuxDayUrls()
	{
	}

//	private static final String SCHEDULE_URL = "https://fosdem.org/schedule/xml";
	private static final String SCHEDULE_URL = "http://10.0.2.2:8080/conference";

	private static final String EVENT_URL_FORMAT = "https://fosdem.org/%1$d/schedule/event/%2$s/";

	private static final String PERSON_URL_FORMAT = "https://fosdem.org/%1$d/schedule/speaker/%2$s/";

	public static String getSchedule()
	{
		return SCHEDULE_URL;
	}

	public static String getEvent(String slug, int year)
	{
		return String.format(Locale.US, EVENT_URL_FORMAT, year, slug);
	}

	public static String getPerson(String slug, int year)
	{
		return String.format(Locale.US, PERSON_URL_FORMAT, year, slug);
	}
}
