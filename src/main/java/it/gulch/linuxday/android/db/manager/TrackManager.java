package it.gulch.linuxday.android.db.manager;


import java.sql.SQLException;
import java.util.List;

import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public interface TrackManager extends BaseORMManager<Track, Long>
{
	List<Track> findByDay(Day day) throws SQLException;
}
