package it.gulch.linuxday.android.db.manager.impl;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;

public class BookmarkManagerTest extends AndroidTestCase
{
	private BookmarkManager bookmarkManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		bookmarkManager = DatabaseManagerFactory.getBookmarkManager(getContext());

		setupDatabase();
	}

	private void setupDatabase()
	{
		OrmLiteDatabaseHelper helper = new OrmLiteDatabaseHelper(getContext());
		SQLiteDatabase database = helper.getWritableDatabase();

		database.execSQL("DELETE FROM bookmark");
		database.execSQL("DELETE FROM event");
		database.execSQL("DELETE FROM event_type");
		database.execSQL("DELETE FROM track");
		database.execSQL("DELETE FROM room");
		database.execSQL("DELETE FROM day");

		database.execSQL("INSERT INTO day (id, name, daydate) VALUES (101, 'Sabato', 1414220400000)");

		database.execSQL("INSERT INTO room (name) VALUES ('B0')");

		database.execSQL("INSERT INTO track (id, title, room_id, day_id) VALUES (301, 'Test', 'B0', 101)");

		database.execSQL("INSERT INTO event_type (code, description) VALUES ('abc', 'ABC')");
		database.execSQL("INSERT INTO event_type (code, description) VALUES ('def', 'DEF')");

		database.execSQL("INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (401, 1414220400000, 1414221600000, 'Evento 1', 'abc', 301)");  // 9:00 to 9:20
		database.execSQL("INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (402, 1414221600000, 1414224000000, 'Evento 2', 'def', 301)");  // 9:20 to 10:00
		database.execSQL("INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (403, 1414224000000, 1414225800000, 'Evento 3', 'abc', 301)");  // 10:00 to 10:30
		database.execSQL("INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (404, 1414225800000, 1414227000000, 'Evento 4', 'abc', 301)");  // 10:30 to 10:50

		database.execSQL("INSERT INTO bookmark (id, event_id) VALUES (101, 401)");
		database.execSQL("INSERT INTO bookmark (id, event_id) VALUES (102, 402)");
		database.execSQL("INSERT INTO bookmark (id, event_id) VALUES (103, 403)");
	}

	public void testGet()
	{
		long bookmarkId = 101L;
		Bookmark bookmark = bookmarkManager.get(bookmarkId);
		Assert.assertNotNull(bookmark);

		Assert.assertEquals(Long.valueOf(bookmarkId), bookmark.getId());
		Assert.assertNotNull(bookmark.getEvent());
		Assert.assertEquals(Long.valueOf(401), bookmark.getEvent().getId());
	}

	public void testGetAll()
	{
		List<Bookmark> bookmarks = bookmarkManager.getAll();
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(bookmarks.size(), 3);
	}

	public void testSave() throws SQLException
	{
		Event event = new Event();
		event.setId(404L);

		Bookmark bookmark = new Bookmark();
		bookmark.setEvent(event);

		Assert.assertNull(bookmark.getId());
		bookmarkManager.save(bookmark);

		Assert.assertNotNull(bookmark.getId());

		Bookmark savedBookmark = bookmarkManager.get(bookmark.getId());
		Assert.assertNotNull(savedBookmark.getEvent());
		Assert.assertEquals(event.getId(), savedBookmark.getEvent().getId());
	}

	public void testSaveOrUpdateWithInexistentBookmark() throws SQLException
	{
		Event event = new Event();
		event.setId(404L);

		Bookmark bookmark = new Bookmark();
		bookmark.setEvent(event);

		Assert.assertNull(bookmark.getId());
		bookmarkManager.saveOrUpdate(bookmark);

		Assert.assertNotNull(bookmark.getId());

		Bookmark savedBookmark = bookmarkManager.get(bookmark.getId());
		Assert.assertNotNull(savedBookmark.getEvent());
		Assert.assertEquals(event.getId(), savedBookmark.getEvent().getId());
	}

	public void testSaveOrUpdateWithExistentBookmark() throws SQLException
	{
		Event event = new Event();
		event.setId(401L);

		long bookmarkId = 101L;
		Bookmark bookmark = new Bookmark();
		bookmark.setId(bookmarkId);
		bookmark.setEvent(event);

		bookmarkManager.saveOrUpdate(bookmark);

		Bookmark savedBookmark = bookmarkManager.get(bookmarkId);
		Assert.assertNotNull(savedBookmark.getEvent());
		Assert.assertEquals(event.getId(), savedBookmark.getEvent().getId());
	}

	public void testUpdate() throws SQLException
	{
		Event event = new Event();
		event.setId(401L);

		long bookmarkId = 101L;
		Bookmark bookmark = new Bookmark();
		bookmark.setId(bookmarkId);
		bookmark.setEvent(event);

		bookmarkManager.update(bookmark);

		Bookmark savedBookmark = bookmarkManager.get(bookmarkId);
		Assert.assertNotNull(savedBookmark.getEvent());
		Assert.assertEquals(event.getId(), savedBookmark.getEvent().getId());
	}

	public void testDelete() throws SQLException
	{
		long bookmarkId = 101L;
		Bookmark bookmark = new Bookmark();
		bookmark.setId(bookmarkId);

		bookmarkManager.delete(bookmark);

		Bookmark bookmarkAfterDelete = bookmarkManager.get(bookmarkId);
		Assert.assertNull(bookmarkAfterDelete);
	}

	public void testTruncate() throws SQLException
	{
		bookmarkManager.truncate();

		List<Bookmark> bookmarks = bookmarkManager.getAll();
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(bookmarks.size(), 0);
	}

	public void testExists() throws SQLException
	{
		boolean bookmarkExists = bookmarkManager.exists(101L);
		Assert.assertTrue(bookmarkExists);
	}

	public void testNotExists() throws SQLException
	{
		boolean bookmarkExists = bookmarkManager.exists(110L);
		Assert.assertFalse(bookmarkExists);
	}

	public void testDeleteOldBookmarks() throws SQLException
	{
		bookmarkManager.deleteOldBookmarks(403L);
		List<Bookmark> bookmarks = bookmarkManager.getAll();
		Assert.assertEquals(bookmarks.size(), 1);
		Bookmark bookmark = bookmarks.get(0);
		Assert.assertEquals(Long.valueOf(403L), bookmark.getEvent().getId());
	}

	public void testAddBookmark() throws SQLException
	{
		Event event = new Event();
		event.setId(404L);

		List<Bookmark> bookmarksBefore = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksBefore.size(), 3);
		bookmarkManager.addBookmark(event);
		List<Bookmark> bookmarksAfter = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksAfter.size(), 4);
	}

	public void testRemoveBookmark() throws SQLException
	{
		Event event = new Event();
		event.setId(401L);

		List<Bookmark> bookmarksBefore = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksBefore.size(), 3);
		bookmarkManager.removeBookmark(event);
		List<Bookmark> bookmarksAfter = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksAfter.size(), 2);
	}

	public void testRemoveBookmarksByEventId() throws SQLException
	{
		List<Long> eventIds = new ArrayList<>();
		eventIds.add(401L);
		eventIds.add(403L);

		List<Bookmark> bookmarksBefore = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksBefore.size(), 3);
		bookmarkManager.removeBookmarksByEventId(eventIds);
		List<Bookmark> bookmarksAfter = bookmarkManager.getAll();
		Assert.assertEquals(bookmarksAfter.size(), 1);
	}

	public void testGetBookmarks() throws SQLException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 50);
		calendar.set(Calendar.SECOND, 0);

		Date date = calendar.getTime();

		List<Bookmark> bookmarks = bookmarkManager.getBookmarks(date);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(1, bookmarks.size());
		Assert.assertEquals(Long.valueOf(103L), bookmarks.get(0).getId());
	}

	public void testGetBookmarks2() throws SQLException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Date date = calendar.getTime();

		List<Bookmark> bookmarks = bookmarkManager.getBookmarks(date);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(0, bookmarks.size());
	}

	public void testGetBookmarks3() throws SQLException
	{
		List<Bookmark> bookmarks = bookmarkManager.getBookmarks(null);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(3, bookmarks.size());
	}
}