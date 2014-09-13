package it.gulch.linuxday.android.db.manager;

import java.sql.SQLException;

import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 07/09/14.
 */
public interface EventManager extends BaseORMManager<Event, Long>
{
	long countEvents() throws SQLException;
}
