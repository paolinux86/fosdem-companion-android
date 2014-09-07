package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public class EventManagerImpl extends BaseORMManagerImpl<Event, Long> implements EventManager
{
	private static final String TAG = EventManagerImpl.class.getSimpleName();

	@Override
	public Event get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Event> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Event object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void update(Event object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Event object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Event> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}
}
