package it.gulch.linuxday.android.constants;

import android.net.Uri;

/**
 * Created by paolo on 14/09/14.
 */
public class UriConstants
{
	public static final Uri URI_TRACKS = Uri.parse("sqlite://it.gulch.linuxday.android/tracks");

	public static final Uri URI_EVENTS = Uri.parse("sqlite://it.gulch.linuxday.android/events");

	private UriConstants()
	{
	}
}
