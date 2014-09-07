package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 07/09/14.
 */
public class DayManagerImpl extends BaseORMManagerImpl<Day, Long> implements DayManager
{
	private static final String TAG = DayManagerImpl.class.getSimpleName();

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
			return dao.queryForAll();
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
}
