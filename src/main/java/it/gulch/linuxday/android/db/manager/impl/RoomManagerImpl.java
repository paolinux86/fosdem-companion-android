package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.RoomManager;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Room;

/**
 * Created by paolo on 07/09/14.
 */
public class RoomManagerImpl extends BaseORMManagerImpl<Room, String> implements RoomManager
{
	private static final String TAG = RoomManagerImpl.class.getSimpleName();

	@Override
	public Room get(String id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Room> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Room object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(Room object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(Room object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Room object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Room> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}
}
