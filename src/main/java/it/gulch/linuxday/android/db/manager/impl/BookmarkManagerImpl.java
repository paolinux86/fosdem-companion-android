package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 07/09/14.
 */
public class BookmarkManagerImpl extends BaseORMManagerImpl<Bookmark, Long> implements BookmarkManager
{
	private static final String TAG = BookmarkManagerImpl.class.getSimpleName();

	@Override
	public Bookmark get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Bookmark> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Bookmark object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void update(Bookmark object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Bookmark object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Bookmark> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}
}
