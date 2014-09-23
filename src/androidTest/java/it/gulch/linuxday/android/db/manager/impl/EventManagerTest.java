package it.gulch.linuxday.android.db.manager.impl;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.EventType;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 22/09/14.
 */
public class EventManagerTest extends AndroidTestCase
{
	private EventManager eventManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		eventManager = DatabaseManagerFactory.getEventManager(getContext());

		setupDatabase();
	}

	private void setupDatabase()
	{
		OrmLiteDatabaseHelper helper = new OrmLiteDatabaseHelper(getContext());
		SQLiteDatabase database = helper.getWritableDatabase();

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

		database.execSQL(
				"INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (401, 1414220400000," +
				" 1414221600000, 'Evento 1', 'abc', 301)");  // 9:00 to 9:20
		database.execSQL(
				"INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (402, 1414221600000," +
				" 1414224000000, 'Evento 2', 'def', 301)");  // 9:20 to 10:00
		database.execSQL(
				"INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (403, 1414224000000," +
				" 1414225800000, 'Evento 3', 'abc', 301)");  // 10:00 to 10:30
		database.execSQL(
				"INSERT INTO event (id, startdate, enddate, title, eventtype_id, track_id) VALUES (404, 1414225800000," +
				" 1414227000000, 'Evento 4', 'abc', 301)");  // 10:30 to 10:50
	}

	public void testGet()
	{
		long eventId = 401L;
		Event event = eventManager.get(eventId);
		Assert.assertNotNull(event);

		Assert.assertEquals(Long.valueOf(eventId), event.getId());

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.YEAR, 2014);
		startDate.set(Calendar.MONTH, Calendar.OCTOBER);
		startDate.set(Calendar.DAY_OF_MONTH, 25);
		startDate.set(Calendar.HOUR_OF_DAY, 9);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);

		boolean isStartDateCorrect =
				DateUtils.truncatedEquals(event.getStartDate(), startDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isStartDateCorrect);

		Calendar endDate = GregorianCalendar.getInstance();
		endDate.set(Calendar.YEAR, 2014);
		endDate.set(Calendar.MONTH, Calendar.OCTOBER);
		endDate.set(Calendar.DAY_OF_MONTH, 25);
		endDate.set(Calendar.HOUR_OF_DAY, 9);
		endDate.set(Calendar.MINUTE, 20);
		endDate.set(Calendar.SECOND, 0);

		boolean isEndDateCorrect = DateUtils.truncatedEquals(event.getEndDate(), endDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isEndDateCorrect);

		Assert.assertEquals("Evento 1", event.getTitle());
		Assert.assertEquals("abc", event.getEventType().getCode());
		Assert.assertEquals(Long.valueOf(301L), event.getTrack().getId());
	}

	public void testGetAll()
	{
		List<Event> events = eventManager.getAll();
		Assert.assertNotNull(events);
		Assert.assertEquals(events.size(), 4);
	}

	public void testSave() throws SQLException
	{
		List<Event> eventsBefore = eventManager.getAll();
		Assert.assertEquals(eventsBefore.size(), 4);

		Event event = new Event();
		event.setId(500L);

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.YEAR, 2014);
		startDate.set(Calendar.MONTH, Calendar.OCTOBER);
		startDate.set(Calendar.DAY_OF_MONTH, 27);
		startDate.set(Calendar.HOUR_OF_DAY, 10);
		startDate.set(Calendar.MINUTE, 50);
		startDate.set(Calendar.SECOND, 0);
		event.setStartDate(startDate.getTime());

		Calendar endDate = GregorianCalendar.getInstance();
		endDate.set(Calendar.YEAR, 2014);
		endDate.set(Calendar.MONTH, Calendar.OCTOBER);
		endDate.set(Calendar.DAY_OF_MONTH, 27);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.MINUTE, 20);
		endDate.set(Calendar.SECOND, 0);
		event.setEndDate(endDate.getTime());

		EventType eventType = new EventType();
		eventType.setCode("abc");
		event.setEventType(eventType);

		Track track = new Track();
		track.setId(301L);
		event.setTrack(track);

		event.setTitle("Evento 5");

		eventManager.save(event);

		List<Event> eventsAfter = eventManager.getAll();
		Assert.assertEquals(eventsAfter.size(), 5);

		Event savedEvent = eventManager.get(event.getId());
		Assert.assertEquals(savedEvent.getId(), event.getId());

		boolean isStartDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getStartDate(), startDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isStartDateCorrect);

		boolean isEndDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getEndDate(), endDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isEndDateCorrect);

		Assert.assertEquals("Evento 5", savedEvent.getTitle());
		Assert.assertEquals("abc", savedEvent.getEventType().getCode());
		Assert.assertEquals(Long.valueOf(301L), savedEvent.getTrack().getId());
	}

	public void testSaveOrUpdateWithInexistentEvent() throws SQLException
	{
		List<Event> eventsBefore = eventManager.getAll();
		Assert.assertEquals(eventsBefore.size(), 4);

		Event event = new Event();
		event.setId(500L);

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.YEAR, 2014);
		startDate.set(Calendar.MONTH, Calendar.OCTOBER);
		startDate.set(Calendar.DAY_OF_MONTH, 27);
		startDate.set(Calendar.HOUR_OF_DAY, 10);
		startDate.set(Calendar.MINUTE, 50);
		startDate.set(Calendar.SECOND, 0);
		event.setStartDate(startDate.getTime());

		Calendar endDate = GregorianCalendar.getInstance();
		endDate.set(Calendar.YEAR, 2014);
		endDate.set(Calendar.MONTH, Calendar.OCTOBER);
		endDate.set(Calendar.DAY_OF_MONTH, 27);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.MINUTE, 20);
		endDate.set(Calendar.SECOND, 0);
		event.setEndDate(endDate.getTime());

		EventType eventType = new EventType();
		eventType.setCode("abc");
		event.setEventType(eventType);

		Track track = new Track();
		track.setId(301L);
		event.setTrack(track);

		event.setTitle("Evento 5");

		eventManager.saveOrUpdate(event);

		List<Event> eventsAfter = eventManager.getAll();
		Assert.assertEquals(eventsAfter.size(), 5);

		Event savedEvent = eventManager.get(event.getId());
		Assert.assertEquals(savedEvent.getId(), event.getId());

		boolean isStartDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getStartDate(), startDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isStartDateCorrect);

		boolean isEndDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getEndDate(), endDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isEndDateCorrect);

		Assert.assertEquals("Evento 5", savedEvent.getTitle());
		Assert.assertEquals("abc", savedEvent.getEventType().getCode());
		Assert.assertEquals(Long.valueOf(301L), savedEvent.getTrack().getId());
	}

	public void testSaveOrUpdateWithExistentEvent() throws SQLException
	{
		List<Event> eventsBefore = eventManager.getAll();
		Assert.assertEquals(eventsBefore.size(), 4);

		Event event = new Event();
		event.setId(401L);

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.YEAR, 2014);
		startDate.set(Calendar.MONTH, Calendar.OCTOBER);
		startDate.set(Calendar.DAY_OF_MONTH, 27);
		startDate.set(Calendar.HOUR_OF_DAY, 10);
		startDate.set(Calendar.MINUTE, 50);
		startDate.set(Calendar.SECOND, 0);
		event.setStartDate(startDate.getTime());

		Calendar endDate = GregorianCalendar.getInstance();
		endDate.set(Calendar.YEAR, 2014);
		endDate.set(Calendar.MONTH, Calendar.OCTOBER);
		endDate.set(Calendar.DAY_OF_MONTH, 27);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.MINUTE, 20);
		endDate.set(Calendar.SECOND, 0);
		event.setEndDate(endDate.getTime());

		EventType eventType = new EventType();
		eventType.setCode("abc");
		event.setEventType(eventType);

		Track track = new Track();
		track.setId(301L);
		event.setTrack(track);

		event.setTitle("Evento 5");

		eventManager.saveOrUpdate(event);

		List<Event> eventsAfter = eventManager.getAll();
		Assert.assertEquals(eventsAfter.size(), 4);

		Event savedEvent = eventManager.get(event.getId());
		Assert.assertEquals(savedEvent.getId(), event.getId());

		boolean isStartDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getStartDate(), startDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isStartDateCorrect);

		boolean isEndDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getEndDate(), endDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isEndDateCorrect);

		Assert.assertEquals("Evento 5", savedEvent.getTitle());
		Assert.assertEquals("abc", savedEvent.getEventType().getCode());
		Assert.assertEquals(Long.valueOf(301L), savedEvent.getTrack().getId());
	}

	public void testUpdate() throws SQLException
	{
		List<Event> eventsBefore = eventManager.getAll();
		Assert.assertEquals(eventsBefore.size(), 4);

		Event event = new Event();
		event.setId(401L);

		Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.YEAR, 2014);
		startDate.set(Calendar.MONTH, Calendar.OCTOBER);
		startDate.set(Calendar.DAY_OF_MONTH, 27);
		startDate.set(Calendar.HOUR_OF_DAY, 10);
		startDate.set(Calendar.MINUTE, 50);
		startDate.set(Calendar.SECOND, 0);
		event.setStartDate(startDate.getTime());

		Calendar endDate = GregorianCalendar.getInstance();
		endDate.set(Calendar.YEAR, 2014);
		endDate.set(Calendar.MONTH, Calendar.OCTOBER);
		endDate.set(Calendar.DAY_OF_MONTH, 27);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.MINUTE, 20);
		endDate.set(Calendar.SECOND, 0);
		event.setEndDate(endDate.getTime());

		EventType eventType = new EventType();
		eventType.setCode("abc");
		event.setEventType(eventType);

		Track track = new Track();
		track.setId(301L);
		event.setTrack(track);

		event.setTitle("Evento 5");

		eventManager.update(event);

		List<Event> eventsAfter = eventManager.getAll();
		Assert.assertEquals(eventsAfter.size(), 4);

		Event savedEvent = eventManager.get(event.getId());
		Assert.assertEquals(savedEvent.getId(), event.getId());

		boolean isStartDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getStartDate(), startDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isStartDateCorrect);

		boolean isEndDateCorrect =
				DateUtils.truncatedEquals(savedEvent.getEndDate(), endDate.getTime(), Calendar.SECOND);
		Assert.assertTrue(isEndDateCorrect);

		Assert.assertEquals("Evento 5", savedEvent.getTitle());
		Assert.assertEquals("abc", savedEvent.getEventType().getCode());
		Assert.assertEquals(Long.valueOf(301L), savedEvent.getTrack().getId());
	}

	public void testDelete() throws SQLException
	{
		long eventId = 401L;
		Event event = new Event();
		event.setId(eventId);

		eventManager.delete(event);

		Event eventAfterDelete = eventManager.get(eventId);
		Assert.assertNull(eventAfterDelete);
	}

	public void testTruncate() throws SQLException
	{
		eventManager.truncate();

		List<Event> events = eventManager.getAll();
		Assert.assertNotNull(events);
		Assert.assertEquals(events.size(), 0);
	}

	public void testExists() throws SQLException
	{
		boolean eventExists = eventManager.exists(401L);
		Assert.assertTrue(eventExists);
	}

	public void testNotExists() throws SQLException
	{
		boolean eventExists = eventManager.exists(410L);
		Assert.assertFalse(eventExists);
	}

	public void testCountEvents() throws SQLException
	{
		long numberOfEvents = eventManager.countEvents();
		Assert.assertEquals(4, numberOfEvents);
	}

	public void testSearch() throws SQLException
	{
		try {
			eventManager.search(null, null, null, DatabaseOrder.ASCENDING);
			Assert.fail("search should have failed with all parameters null");
		} catch(IllegalArgumentException e) {
			// do nothing
		}
	}

	public void testSearch2() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 9);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(minStartDate.getTime(), null, null, DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(404L), result.get(1).getId());
	}

	public void testSearch2R() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 9);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(minStartDate.getTime(), null, null, DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(404L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(403L), result.get(1).getId());
	}

	public void testSearch3() throws SQLException
	{
		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 9);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(null, maxStartDate.getTime(), null, DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(401L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(402L), result.get(1).getId());
	}

	public void testSearch3R() throws SQLException
	{
		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 9);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(null, maxStartDate.getTime(), null, DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(402L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(401L), result.get(1).getId());
	}

	public void testSearch4() throws SQLException
	{
		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 20);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(null, null, minEndDate.getTime(), DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(404L), result.get(1).getId());
	}

	public void testSearch4R() throws SQLException
	{
		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 20);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager.search(null, null, minEndDate.getTime(), DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(404L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(403L), result.get(1).getId());
	}

	public void testSearch5() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 9);
		maxStartDate.set(Calendar.MINUTE, 30);
		maxStartDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(minStartDate.getTime(), maxStartDate.getTime(), null, DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(401L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(402L), result.get(1).getId());
	}

	public void testSearch5R() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 9);
		maxStartDate.set(Calendar.MINUTE, 30);
		maxStartDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(minStartDate.getTime(), maxStartDate.getTime(), null, DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(402L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(401L), result.get(1).getId());
	}

	public void testSearch6() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 10);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(minStartDate.getTime(), null, minEndDate.getTime(), DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(404L), result.get(1).getId());
	}

	public void testSearch6R() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 10);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(minStartDate.getTime(), null, minEndDate.getTime(), DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Long.valueOf(404L), result.get(0).getId());
		Assert.assertEquals(Long.valueOf(403L), result.get(1).getId());
	}

	public void testSearch7() throws SQLException
	{
		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 10);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 25);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(null, maxStartDate.getTime(), minEndDate.getTime(), DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
	}

	public void testSearch7R() throws SQLException
	{
		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 10);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 25);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result =
				eventManager.search(null, maxStartDate.getTime(), minEndDate.getTime(), DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
	}

	public void testSearch8() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 10);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 25);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager
				.search(minStartDate.getTime(), maxStartDate.getTime(), minEndDate.getTime(), DatabaseOrder.ASCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
	}

	public void testSearch8R() throws SQLException
	{
		Calendar minStartDate = GregorianCalendar.getInstance();
		minStartDate.set(Calendar.YEAR, 2014);
		minStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minStartDate.set(Calendar.DAY_OF_MONTH, 25);
		minStartDate.set(Calendar.HOUR_OF_DAY, 8);
		minStartDate.set(Calendar.MINUTE, 50);
		minStartDate.set(Calendar.SECOND, 0);

		Calendar maxStartDate = GregorianCalendar.getInstance();
		maxStartDate.set(Calendar.YEAR, 2014);
		maxStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
		maxStartDate.set(Calendar.DAY_OF_MONTH, 25);
		maxStartDate.set(Calendar.HOUR_OF_DAY, 10);
		maxStartDate.set(Calendar.MINUTE, 25);
		maxStartDate.set(Calendar.SECOND, 0);

		Calendar minEndDate = GregorianCalendar.getInstance();
		minEndDate.set(Calendar.YEAR, 2014);
		minEndDate.set(Calendar.MONTH, Calendar.OCTOBER);
		minEndDate.set(Calendar.DAY_OF_MONTH, 25);
		minEndDate.set(Calendar.HOUR_OF_DAY, 10);
		minEndDate.set(Calendar.MINUTE, 25);
		minEndDate.set(Calendar.SECOND, 0);

		List<Event> result = eventManager
				.search(minStartDate.getTime(), maxStartDate.getTime(), minEndDate.getTime(), DatabaseOrder.DESCENDING);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Long.valueOf(403L), result.get(0).getId());
	}
}
