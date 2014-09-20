package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public class TrackManagerImpl implements TrackManager
{
	private static final String TAG = TrackManagerImpl.class.getSimpleName();

	private Dao<Track, Long> dao;

	private TrackManagerImpl()
	{
	}

	public static TrackManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		TrackManagerImpl trackManager = new TrackManagerImpl();
		trackManager.dao = helper.getDao(Track.class);

		return trackManager;
	}

	@Override
	public Track get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Track> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Track object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(Track object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(Track object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Track object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Track> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}

	@Override
	public boolean exists(Long objectId) throws SQLException
	{
		return dao.idExists(objectId);
	}

	@Override
	public List<Track> findByDay(Day day) throws SQLException
	{
		QueryBuilder<Track, Long> queryBuilder = dao.queryBuilder();
		queryBuilder.where().eq("day_id", day.getId());
		queryBuilder.orderBy("id", true);

		return dao.query(queryBuilder.prepare());
	}
}
