package it.gulch.linuxday.android.db.manager.impl;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 22/09/14.
 */
public class DayManagerTest extends AndroidTestCase
{
	private DayManager dayManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		dayManager = DatabaseManagerFactory.getDayManager(getContext());

		setupDatabase();
	}

	private void setupDatabase()
	{
		OrmLiteDatabaseHelper helper = new OrmLiteDatabaseHelper(getContext());
		SQLiteDatabase database = helper.getWritableDatabase();

		database.execSQL("DELETE FROM day");

		database.execSQL("INSERT INTO day (id, name, daydate) VALUES (101, 'Sabato', 1414220400000)");
	}

	public void testGet()
	{
		long dayId = 101L;
		Day day = dayManager.get(dayId);
		Assert.assertNotNull(day);

		Assert.assertEquals(Long.valueOf(dayId), day.getId());
		Assert.assertEquals("Sabato", day.getName());

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		boolean areDatesEqual = DateUtils.truncatedEquals(day.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	public void testGetAll()
	{
		List<Day> days = dayManager.getAll();
		Assert.assertNotNull(days);
		Assert.assertEquals(days.size(), 1);
	}

	public void testSave() throws SQLException
	{
		List<Day> daysBefore = dayManager.getAll();
		Assert.assertEquals(daysBefore.size(), 1);

		Day day = new Day();
		day.setId(100L);
		day.setName("Lunedì");

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		day.setDayDate(calendar.getTime());

		dayManager.save(day);

		List<Day> daysAfter = dayManager.getAll();
		Assert.assertEquals(daysAfter.size(), 2);

		Day savedDay = dayManager.get(day.getId());
		Assert.assertEquals(savedDay.getId(), day.getId());
		Assert.assertEquals(savedDay.getName(), day.getName());

		boolean areDatesEqual = DateUtils.truncatedEquals(savedDay.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	public void testSaveOrUpdateWithInexistentDay() throws SQLException
	{
		List<Day> daysBefore = dayManager.getAll();
		Assert.assertEquals(daysBefore.size(), 1);

		Day day = new Day();
		day.setId(100L);
		day.setName("Lunedì");

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		day.setDayDate(calendar.getTime());

		dayManager.saveOrUpdate(day);

		List<Day> daysAfter = dayManager.getAll();
		Assert.assertEquals(daysAfter.size(), 2);

		Day savedDay = dayManager.get(day.getId());
		Assert.assertEquals(savedDay.getId(), day.getId());
		Assert.assertEquals(savedDay.getName(), day.getName());

		boolean areDatesEqual = DateUtils.truncatedEquals(savedDay.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	public void testSaveOrUpdateWithExistentDay() throws SQLException
	{
		doBeforeChecks();

		Day day = new Day();
		day.setId(101L);
		day.setName("Giovedì");

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		day.setDayDate(calendar.getTime());

		dayManager.saveOrUpdate(day);

		List<Day> daysAfter = dayManager.getAll();
		Assert.assertEquals(daysAfter.size(), 1);

		Day savedDay = dayManager.get(day.getId());
		Assert.assertEquals(savedDay.getId(), day.getId());
		Assert.assertEquals(savedDay.getName(), day.getName());

		boolean areDatesEqual = DateUtils.truncatedEquals(savedDay.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	private void doBeforeChecks()
	{
		List<Day> daysBefore = dayManager.getAll();
		Assert.assertEquals(daysBefore.size(), 1);

		Day dayBefore = daysBefore.get(0);
		Assert.assertEquals(Long.valueOf(101L), dayBefore.getId());
		Assert.assertEquals("Sabato", dayBefore.getName());

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		boolean areDatesEqual = DateUtils.truncatedEquals(dayBefore.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	public void testUpdate() throws SQLException
	{
		doBeforeChecks();

		Day day = new Day();
		day.setId(101L);
		day.setName("Giovedì");

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		day.setDayDate(calendar.getTime());

		dayManager.update(day);

		List<Day> daysAfter = dayManager.getAll();
		Assert.assertEquals(daysAfter.size(), 1);

		Day savedDay = dayManager.get(day.getId());
		Assert.assertEquals(savedDay.getId(), day.getId());
		Assert.assertEquals(savedDay.getName(), day.getName());

		boolean areDatesEqual = DateUtils.truncatedEquals(savedDay.getDayDate(), calendar.getTime(), Calendar.SECOND);
		Assert.assertTrue(areDatesEqual);
	}

	public void testDelete() throws SQLException
	{
		long dayId = 101L;
		Day day = new Day();
		day.setId(dayId);

		dayManager.delete(day);

		Day dayAfterDelete = dayManager.get(dayId);
		Assert.assertNull(dayAfterDelete);
	}

	public void testTruncate() throws SQLException
	{
		dayManager.truncate();

		List<Day> days = dayManager.getAll();
		Assert.assertNotNull(days);
		Assert.assertEquals(days.size(), 0);
	}

}
