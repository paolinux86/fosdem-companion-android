package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 07/09/14.
 */
public class DayManagerImpl implements DayManager
{
	private static final String TAG = DayManagerImpl.class.getSimpleName();

	private Dao<Day, Long> dao;

	private List<Day> cachedDays;

	private DayManagerImpl()
	{
	}

	public static DayManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		DayManagerImpl dayManager = new DayManagerImpl();
		dayManager.dao = helper.getDao(Day.class);

		return dayManager;
	}

	@Override
	public Day get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Day> getAll()
	{
		try {
			cachedDays = dao.queryForAll();
			return cachedDays;
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Day object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(Day object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(Day object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Day object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Day> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}

	@Override
	public boolean exists(Long objectId) throws SQLException
	{
		return dao.idExists(objectId);
	}

	@Override
	public List<Day> getCachedDays()
	{
		return cachedDays;
	}
}
