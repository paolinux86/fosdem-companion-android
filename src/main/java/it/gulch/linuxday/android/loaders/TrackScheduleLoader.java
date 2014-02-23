package it.gulch.linuxday.android.loaders;

import android.content.Context;
import android.database.Cursor;
import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.model.Day;
import it.gulch.linuxday.android.model.Track;

public class TrackScheduleLoader extends SimpleCursorLoader {

	private final Day day;
	private final Track track;

	public TrackScheduleLoader(Context context, Day day, Track track) {
		super(context);
		this.day = day;
		this.track = track;
	}

	@Override
	protected Cursor getCursor() {
		return DatabaseManager.getInstance().getEvents(day, track);
	}
}
