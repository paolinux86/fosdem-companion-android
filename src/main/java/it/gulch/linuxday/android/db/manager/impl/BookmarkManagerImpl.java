package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.BaseORMManager;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 07/09/14.
 */
public class BookmarkManagerImpl implements BookmarkManager
{
	private static final String TAG = BookmarkManagerImpl.class.getSimpleName();

	private Dao<Bookmark, Long> dao;

	private BookmarkManagerImpl()
	{
	}

	public static BookmarkManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		BookmarkManagerImpl bookmarkManager = new BookmarkManagerImpl();
		bookmarkManager.dao = helper.getDao(Bookmark.class);

		return bookmarkManager;
	}

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
	public void saveOrUpdate(Bookmark object) throws SQLException
	{
		dao.createOrUpdate(object);
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

	@Override
	public boolean exists(Long objectId) throws SQLException
	{
		return dao.idExists(objectId);
	}

	@Override
	public void deleteOldBookmarks(Long minEventId) throws SQLException
	{
		DeleteBuilder<Bookmark, Long> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().lt("event_id", minEventId);
		PreparedDelete<Bookmark> preparedDelete = deleteBuilder.prepare();
		dao.delete(preparedDelete);
	}

	@Override
	public void addBookmark(Event event) throws SQLException
	{
		Bookmark bookmark = new Bookmark();
		bookmark.setEvent(event);

		save(bookmark);
	}

	@Override
	public void removeBookmark(Event event) throws SQLException
	{
		DeleteBuilder<Bookmark, Long> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq("event_id", event.getId());

		dao.delete(deleteBuilder.prepare());
	}

	@Override
	public void removeBookmarksByEventId(long[] eventIds) throws SQLException
	{
		DeleteBuilder<Bookmark, Long> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().in("event_id", eventIds);

		dao.delete(deleteBuilder.prepare());
	}

	@Override
	public List<Bookmark> getBookmarks(long minStartTime)
	{
		// TODO: implementare
		return Collections.emptyList();
	}
}
