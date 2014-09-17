package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.EventTypeManager;
import it.gulch.linuxday.android.model.db.EventType;

/**
 * Created by paolo on 07/09/14.
 */
public class EventTypeManagerImpl implements EventTypeManager
{
	private static final String TAG = EventTypeManagerImpl.class.getSimpleName();

	private Dao<EventType, String> dao;

	private EventTypeManagerImpl()
	{
	}

	public static EventTypeManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		EventTypeManagerImpl eventTypeManager = new EventTypeManagerImpl();
		eventTypeManager.dao = helper.getDao(EventType.class);

		return eventTypeManager;
	}

	@Override
	public EventType get(String id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<EventType> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(EventType object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(EventType object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(EventType object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(EventType object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<EventType> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}

	@Override
	public boolean exists(String objectId) throws SQLException
	{
		return dao.idExists(objectId);
	}
}
