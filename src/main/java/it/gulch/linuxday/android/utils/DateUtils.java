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
package it.gulch.linuxday.android.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils
{
	private static final TimeZone BELGIUM_TIME_ZONE = TimeZone.getTimeZone("GMT+1");

	private static final DateFormat TIME_DATE_FORMAT =
		withBelgiumTimeZone(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault()));

	public static TimeZone getBelgiumTimeZone()
	{
		return BELGIUM_TIME_ZONE;
	}

	public static DateFormat withBelgiumTimeZone(DateFormat format)
	{
		format.setTimeZone(BELGIUM_TIME_ZONE);
		return format;
	}

	public static DateFormat getTimeDateFormat()
	{
		return TIME_DATE_FORMAT;
	}
}
