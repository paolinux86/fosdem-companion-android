package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 18/09/14.
 */
public class TrackScheduleLoader extends SimpleDatabaseLoader<List<Event>>
{
	private final Track track;

	private EventManager eventManager;

	public TrackScheduleLoader(Context context, EventManager eventManager, Track track)
	{
		super(context);
		this.eventManager = eventManager;
		this.track = track;
	}

	@Override
	protected List<Event> getObject()
	{
		try {
			return eventManager.searchEventsByTrack(track);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}