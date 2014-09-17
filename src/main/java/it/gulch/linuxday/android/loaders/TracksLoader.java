package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 17/09/14.
 */
public class TracksLoader extends SimpleDatabaseLoader<List<Track>>
{
	private final Day day;

	private TrackManager trackManager;

	public TracksLoader(Context context, TrackManager trackManager, Day day)
	{
		super(context);
		this.trackManager = trackManager;
		this.day = day;
	}

	@Override
	protected List<Track> getObject()
	{
		try {
			return trackManager.findByDay(day);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}