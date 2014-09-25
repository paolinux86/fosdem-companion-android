package it.gulch.linuxday.android.db.manager;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Person;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public interface EventManager extends BaseORMManager<Event, Long>
{
	long countEvents() throws SQLException;

	List<Event> search(Date minStartTime, Date maxStartTime, Date minEndTime, DatabaseOrder databaseOrder)
			throws SQLException;

	List<Event> searchEventsByTrack(Track track) throws SQLException;

	List<Event> getBookmarkedEvents(Date minStartTime) throws SQLException;

	List<Event> searchEventsByPerson(Person person) throws SQLException;

	List<Event> search(String query) throws SQLException;
}
