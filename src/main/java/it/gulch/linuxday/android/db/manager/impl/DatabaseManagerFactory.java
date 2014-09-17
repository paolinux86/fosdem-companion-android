package it.gulch.linuxday.android.db.manager.impl;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.EventTypeManager;
import it.gulch.linuxday.android.db.manager.LinkManager;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.db.manager.RoomManager;
import it.gulch.linuxday.android.db.manager.TrackManager;

/**
 * Created by paolo on 17/09/14.
 */
public class DatabaseManagerFactory
{
	private static BookmarkManager bookmarkManager;

	private static DayManager dayManager;

	private static EventManager eventManager;

	private static EventTypeManager eventTypeManager;

	private static LinkManager linkManager;

	private static PersonManager personManager;

	private static RoomManager roomManager;

	private static TrackManager trackManager;

	private DatabaseManagerFactory()
	{
	}

	public static BookmarkManager getBookmarkManager(Context context) throws SQLException
	{
		if(bookmarkManager == null) {
			bookmarkManager = BookmarkManagerImpl.newInstance(getHelper(context));
		}

		return bookmarkManager;
	}

	public static DayManager getDayManager(Context context) throws SQLException
	{
		if(dayManager == null) {
			dayManager = DayManagerImpl.newInstance(getHelper(context));
		}

		return dayManager;
	}

	public static EventManager getEventManager(Context context) throws SQLException
	{
		if(eventManager == null) {
			eventManager = EventManagerImpl.newInstance(getHelper(context));
		}

		return eventManager;
	}

	public static EventTypeManager getEventTypeManager(Context context) throws SQLException
	{
		if(eventTypeManager == null) {
			eventTypeManager = EventTypeManagerImpl.newInstance(getHelper(context));
		}

		return eventTypeManager;
	}

	public static LinkManager getLinkManager(Context context) throws SQLException
	{
		if(linkManager == null) {
			linkManager = LinkManagerImpl.newInstance(getHelper(context));
		}

		return linkManager;
	}

	public static PersonManager getPersonManager(Context context) throws SQLException
	{
		if(personManager == null) {
			personManager = PersonManagerImpl.newInstance(getHelper(context));
		}

		return personManager;
	}

	public static RoomManager getRoomManager(Context context) throws SQLException
	{
		if(roomManager == null) {
			roomManager = RoomManagerImpl.newInstance(getHelper(context));
		}

		return roomManager;
	}

	public static TrackManager getTrackManager(Context context) throws SQLException
	{
		if(trackManager == null) {
			trackManager = TrackManagerImpl.newInstance(getHelper(context));
		}

		return trackManager;
	}

	private static OrmLiteDatabaseHelper getHelper(Context context)
	{
		return OpenHelperManager.getHelper(context, OrmLiteDatabaseHelper.class);
	}
}
