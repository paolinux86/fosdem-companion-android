package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.LinkManager;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Link;

/**
 * Created by paolo on 07/09/14.
 */
public class LinkManagerImpl extends BaseORMManagerImpl<Link, Long> implements LinkManager
{
	private static final String TAG = LinkManagerImpl.class.getSimpleName();

	@Override
	public Link get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Link> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Link object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(Link object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(Link object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Link object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Link> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}
}
