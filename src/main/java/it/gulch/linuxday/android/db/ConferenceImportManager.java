package it.gulch.linuxday.android.db;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import it.gulch.linuxday.android.constants.ActionConstants;
import it.gulch.linuxday.android.constants.UriConstants;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.EventTypeManager;
import it.gulch.linuxday.android.db.manager.LinkManager;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.db.manager.RoomManager;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.exceptions.ImportException;
import it.gulch.linuxday.android.model.json.Conference;
import it.gulch.linuxday.android.model.json.Day;
import it.gulch.linuxday.android.model.json.Event;
import it.gulch.linuxday.android.model.json.EventType;
import it.gulch.linuxday.android.model.json.Link;
import it.gulch.linuxday.android.model.json.Person;
import it.gulch.linuxday.android.model.json.Room;
import it.gulch.linuxday.android.model.json.Track;
import it.gulch.linuxday.android.services.PreferencesService;
import it.gulch.linuxday.android.services.impl.ManagerFactory;

/**
 * Created by paolo on 13/09/14.
 */
public class ConferenceImportManager
{
	private static final String TAG = ConferenceImportManager.class.getSimpleName();

	private BookmarkManager bookmarkManager;

	private DayManager dayManager;

	private EventManager eventManager;

	private EventTypeManager eventTypeManager;

	private LinkManager linkManager;

	private PersonManager personManager;

	private RoomManager roomManager;

	private TrackManager trackManager;

	private PreferencesService preferencesService;

	private Long minEventId;

	private Context context;

	public ConferenceImportManager(Context context)
	{
		try {
			bookmarkManager = DatabaseManagerFactory.getBookmarkManager(context);
			dayManager = DatabaseManagerFactory.getDayManager(context);
			eventManager = DatabaseManagerFactory.getEventManager(context);
			eventTypeManager = DatabaseManagerFactory.getEventTypeManager(context);
			linkManager = DatabaseManagerFactory.getLinkManager(context);
			personManager = DatabaseManagerFactory.getPersonManager(context);
			roomManager = DatabaseManagerFactory.getRoomManager(context);
			trackManager = DatabaseManagerFactory.getTrackManager(context);
			preferencesService = ManagerFactory.getPreferencesService();

			this.context = context;
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public long importConference(final Conference conference) throws ImportException
	{
		if(conference == null || conference.getDays() == null || conference.getDays().size() == 0) {
			// FIXME
			throw new RuntimeException();
		}

		try {
			OrmLiteDatabaseHelper helper = OpenHelperManager.getHelper(context, OrmLiteDatabaseHelper.class);
			long result = (Long) TransactionManager.callInTransaction(helper.getConnectionSource(), new Callable<Object>()
			{
				@Override
				public Object call() throws Exception
				{
					return internalImportConference(conference);
				}
			});

			notifyCompletion();

			return result;
		} catch(SQLException e) {
			throw new ImportException();
		}
	}

	private long internalImportConference(Conference conference) throws ImportException
	{
		try {
			minEventId = Long.MAX_VALUE;
			clearDatabase();
			for(Day day : conference.getDays()) {
				it.gulch.linuxday.android.model.db.Day dbDay = day.toDatabaseDay();
				dayManager.save(dbDay);

				insertTracks(day);
			}

			if(minEventId < Long.MAX_VALUE) {
				bookmarkManager.deleteOldBookmarks(minEventId);
			}

			return eventManager.countEvents();
		} catch(SQLException e) {
			throw new ImportException(e);
		}
	}

	private void insertTracks(Day day) throws SQLException
	{
		if(day.getTracks() == null || day.getTracks().size() == 0) {
			// FIXME
			throw new RuntimeException();
		}

		it.gulch.linuxday.android.model.db.Day dbDay = day.toDatabaseDay();
		for(Track track : day.getTracks()) {
			it.gulch.linuxday.android.model.db.Room room = insertRoom(track.getRoom());

			it.gulch.linuxday.android.model.db.Track dbTrack = track.toDatabaseTrack();
			dbTrack.setRoom(room);
			dbTrack.setDay(dbDay);
			trackManager.save(dbTrack);

			insertEvents(track, dbTrack);
		}
	}

	private it.gulch.linuxday.android.model.db.Room insertRoom(Room room) throws SQLException
	{
		if(room == null) {
			// FIXME
			throw new RuntimeException();
		}

		it.gulch.linuxday.android.model.db.Room dbRoom = room.toDatabaseRoom();
		if(roomManager.exists(room.getName())) {
			return dbRoom;
		}

		roomManager.save(dbRoom);

		return dbRoom;
	}

	private void insertEvents(Track track, it.gulch.linuxday.android.model.db.Track dbTrack) throws SQLException
	{
		if(track.getEvents() == null || track.getEvents().size() == 0) {
			// FIXME
			throw new RuntimeException();
		}

		for(Event event : track.getEvents()) {
			long eventId = event.getId();
			if(eventId < minEventId) {
				minEventId = eventId;
			}

			it.gulch.linuxday.android.model.db.EventType eventType = insertEventType(event.getEventType());

			it.gulch.linuxday.android.model.db.Event dbEvent = event.toDatabaseEvent();
			insertPeople(event, dbEvent);
			dbEvent.setEventType(eventType);
			dbEvent.setTrack(dbTrack);
			eventManager.save(dbEvent);

			insertLinks(event, dbEvent);
		}
	}

	private it.gulch.linuxday.android.model.db.EventType insertEventType(EventType eventType) throws SQLException
	{
		if(eventType == null) {
			// FIXME
			throw new RuntimeException();
		}

		it.gulch.linuxday.android.model.db.EventType dbEventType = eventType.toDatabaseEventType();
		if(eventTypeManager.exists(eventType.getCode())) {
			return dbEventType;
		}

		eventTypeManager.save(dbEventType);

		return dbEventType;
	}

	private void insertLinks(Event event, it.gulch.linuxday.android.model.db.Event dbEvent) throws SQLException
	{
		if(event.getLinks() == null || event.getLinks().size() == 0) {
			return;
		}

		for(Link link : event.getLinks()) {
			it.gulch.linuxday.android.model.db.Link dbLink = link.toDatabaseLink();
			dbLink.setEvent(dbEvent);
			linkManager.save(dbLink);
		}
	}

	private void insertPeople(Event event, it.gulch.linuxday.android.model.db.Event dbEvent) throws SQLException
	{
		if(event.getPeople() == null || event.getPeople().size() == 0) {
			return;
		}

		for(Person person : event.getPeople()) {
			it.gulch.linuxday.android.model.db.Person dbPerson = person.toDatabasePerson();
			if(!personManager.exists(person.getId())) {
				personManager.save(dbPerson);
			}
			dbEvent.addPerson(dbPerson);
		}
	}

	private void notifyCompletion()
	{
		preferencesService.updateLastUpdateTime(context);

		context.getContentResolver().notifyChange(UriConstants.URI_TRACKS, null);
		context.getContentResolver().notifyChange(UriConstants.URI_EVENTS, null);
		LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ActionConstants
																					.ACTION_SCHEDULE_REFRESHED));
	}

	private void clearDatabase() throws SQLException
	{


		eventManager.truncate();
		eventTypeManager.truncate();
		linkManager.truncate();
		personManager.truncate();
		trackManager.truncate();
		roomManager.truncate();
		dayManager.truncate();

		preferencesService.resetLastUpdateTime(context);

		context.getContentResolver().notifyChange(UriConstants.URI_TRACKS, null);
		context.getContentResolver().notifyChange(UriConstants.URI_EVENTS, null);
		LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ActionConstants
																					.ACTION_SCHEDULE_REFRESHED));
	}
}
