package it.gulch.linuxday.android.db;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.sql.SQLException;

import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.DayManager;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.db.manager.EventTypeManager;
import it.gulch.linuxday.android.db.manager.LinkManager;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.db.manager.RoomManager;
import it.gulch.linuxday.android.db.manager.TrackManager;
import it.gulch.linuxday.android.db.manager.impl.BookmarkManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.DayManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.EventManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.EventTypeManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.LinkManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.PersonManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.RoomManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.TrackManagerImpl;
import it.gulch.linuxday.android.model.json.Conference;
import it.gulch.linuxday.android.model.json.Day;
import it.gulch.linuxday.android.model.json.Event;
import it.gulch.linuxday.android.model.json.EventType;
import it.gulch.linuxday.android.model.json.Link;
import it.gulch.linuxday.android.model.json.Person;
import it.gulch.linuxday.android.model.json.Room;
import it.gulch.linuxday.android.model.json.Track;

/**
 * Created by paolo on 13/09/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ConferenceImportManager
{
	@Bean(BookmarkManagerImpl.class)
	BookmarkManager bookmarkManager;

	@Bean(DayManagerImpl.class)
	DayManager dayManager;

	@Bean(EventManagerImpl.class)
	EventManager eventManager;

	@Bean(EventTypeManagerImpl.class)
	EventTypeManager eventTypeManager;

	@Bean(LinkManagerImpl.class)
	LinkManager linkManager;

	@Bean(PersonManagerImpl.class)
	PersonManager personManager;

	@Bean(RoomManagerImpl.class)
	RoomManager roomManager;

	@Bean(TrackManagerImpl.class)
	TrackManager trackManager;

	public long importConference(Conference conference) throws SQLException
	{
		if(conference == null || conference.getDays() == null || conference.getDays().size() == 0) {
			// FIXME
			throw new RuntimeException();
		}

		try {
			clearDatabase();
			for(Day day : conference.getDays()) {
				it.gulch.linuxday.android.model.db.Day dbDay = day.toDatabaseDay();
				dayManager.save(dbDay);

				insertTracks(day);
			}
		} catch(SQLException e) {
			// TODO
			e.printStackTrace();
		}

		return eventManager.countEvents();
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
			personManager.save(dbPerson);
			dbEvent.addPerson(dbPerson);
		}
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
		bookmarkManager.truncate();
	}
}