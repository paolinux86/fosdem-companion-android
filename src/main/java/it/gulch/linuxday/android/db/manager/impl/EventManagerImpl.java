package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Link;
import it.gulch.linuxday.android.model.db.Person;
import it.gulch.linuxday.android.model.db.PersonPresentsEvent;
import it.gulch.linuxday.android.model.db.Room;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public class EventManagerImpl implements EventManager
{
	private static final String TAG = EventManagerImpl.class.getSimpleName();

	private Dao<Event, Long> eventDao;

	private Dao<PersonPresentsEvent, Long> personPresentsEventDao;

	private Dao<Bookmark, Long> bookmarkDao;

	private Dao<Link, Long> linkDao;

	private Dao<Track, Long> trackDao;

	private EventManagerImpl()
	{
	}

	public static EventManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		EventManagerImpl eventManager = new EventManagerImpl();
		eventManager.eventDao = helper.getDao(Event.class);
		eventManager.personPresentsEventDao = helper.getDao(PersonPresentsEvent.class);
		eventManager.bookmarkDao = helper.getDao(Bookmark.class);
		eventManager.linkDao = helper.getDao(Link.class);
		eventManager.trackDao = helper.getDao(Track.class);

		return eventManager;
	}

	@Override
	public Event get(Long id)
	{
		try {
			Event event = eventDao.queryForId(id);
			addPeople(event);
			addLinks(event);
			checkBookmark(event);

			return event;
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	private void addPeople(Event event) throws SQLException
	{
		QueryBuilder<PersonPresentsEvent, Long> queryBuilder = personPresentsEventDao.queryBuilder();
		queryBuilder.where().eq("event_id", event.getId());
		PreparedQuery<PersonPresentsEvent> preparedQuery = queryBuilder.prepare();

		List<Person> people = new ArrayList<Person>();
		List<PersonPresentsEvent> result = personPresentsEventDao.query(preparedQuery);
		for(PersonPresentsEvent personPresentsEvent : result) {
			people.add(personPresentsEvent.getPerson());
		}

		event.setPeople(people);
	}

	private void addLinks(Event event) throws SQLException
	{
		QueryBuilder<Link, Long> queryBuilder = linkDao.queryBuilder();
		queryBuilder.where().eq("event_id", event.getId());
		PreparedQuery<Link> preparedQuery = queryBuilder.prepare();

		List<Link> result = linkDao.query(preparedQuery);
		event.setLinks(result);
	}

	private void checkBookmark(Event event) throws SQLException
	{
		QueryBuilder<Bookmark, Long> queryBuilder = bookmarkDao.queryBuilder();
		queryBuilder.setCountOf(true);
		PreparedQuery<Bookmark> preparedQuery = queryBuilder.where().eq("event_id", event.getId()).prepare();

		Boolean hasBookmark = bookmarkDao.countOf(preparedQuery) > 0;
		event.setBookmarked(hasBookmark);
	}

	@Override
	public List<Event> getAll()
	{
		try {
			List<Event> events = eventDao.queryForAll();
			for(Event event : events) {
				addPeople(event);
				addLinks(event);
				checkBookmark(event);
			}

			return events;
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Event object) throws SQLException
	{
		eventDao.create(object);

		insertPeopleInEvent(object);
	}

	private void insertPeopleInEvent(Event event) throws SQLException
	{
		if(event.getPeople() == null || event.getPeople().size() == 0) {
			return;
		}

		for(Person person : event.getPeople()) {
			PersonPresentsEvent personPresentsEvent = new PersonPresentsEvent();
			personPresentsEvent.setEvent(event);
			personPresentsEvent.setPerson(person);

			personPresentsEventDao.create(personPresentsEvent);
		}
	}

	@Override
	public void saveOrUpdate(Event object) throws SQLException
	{
		Dao.CreateOrUpdateStatus status = eventDao.createOrUpdate(object);

		if(status.isUpdated()) {
			removePeopleInEvent(object);
		}
		insertPeopleInEvent(object);
	}

	private void removePeopleInEvent(Event event) throws SQLException
	{
		DeleteBuilder<PersonPresentsEvent, Long> deleteBuilder = personPresentsEventDao.deleteBuilder();
		deleteBuilder.where().eq("event_id", event.getId());
		PreparedDelete<PersonPresentsEvent> preparedDelete = deleteBuilder.prepare();

		personPresentsEventDao.delete(preparedDelete);
	}

	@Override
	public void update(Event object) throws SQLException
	{
		eventDao.update(object);
		removePeopleInEvent(object);
		insertPeopleInEvent(object);
	}

	@Override
	public void delete(Event object) throws SQLException
	{
		removePeopleInEvent(object);
		eventDao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<PersonPresentsEvent> preparedDelete = personPresentsEventDao.deleteBuilder().prepare();
		personPresentsEventDao.delete(preparedDelete);

		PreparedDelete<Event> eventPreparedDelete = eventDao.deleteBuilder().prepare();
		eventDao.delete(eventPreparedDelete);
	}

	@Override
	public boolean exists(Long objectId) throws SQLException
	{
		return eventDao.idExists(objectId);
	}

	@Override
	public long countEvents() throws SQLException
	{
		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		queryBuilder.setCountOf(true);
		PreparedQuery<Event> preparedQuery = queryBuilder.prepare();

		return eventDao.countOf(preparedQuery);
	}

	@Override
	public List<Event> search(Date minStartTime, Date maxStartTime, Date minEndTime, DatabaseOrder databaseOrder)
			throws SQLException
	{
		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		Where<Event, Long> where = queryBuilder.where();

		int conditions = 0;
		if(minStartTime != null) {
			where.gt("start_date", minStartTime);
			conditions++;
		}
		if(maxStartTime != null) {
			where.lt("start_date", maxStartTime);
			conditions++;
		}
		if(minEndTime != null) {
			where.gt("end_date", minEndTime);
			conditions++;
		}

		if(conditions == 0) {
			throw new IllegalArgumentException("At least one filter must be provided");
		}

		queryBuilder.orderBy("start_date", databaseOrder == DatabaseOrder.ASCENDING);

		List<Event> events = eventDao.query(queryBuilder.prepare());
		for(Event event : events) {
			addPeople(event);
			addLinks(event);
			checkBookmark(event);
		}

		return events;
	}

	@Override
	public List<Event> searchEventsByTrack(Track track) throws SQLException
	{
		if(track == null || track.getId() == null) {
			return Collections.emptyList();
		}

		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		queryBuilder.where().eq("track_id", track.getId());

		List<Event> events = eventDao.query(queryBuilder.prepare());
		for(Event event : events) {
			addPeople(event);
			addLinks(event);
			checkBookmark(event);
		}

		return events;
	}

	// TODO: verificare che funzioni xD
	@Override
	public List<Event> getBookmarkedEvents(Date minStartTime) throws SQLException
	{
		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		queryBuilder.join(bookmarkDao.queryBuilder());
		if(minStartTime != null) {
			queryBuilder.where().gt("start_date", minStartTime);
		}

		List<Event> events = eventDao.query(queryBuilder.prepare());
		for(Event event : events) {
			addPeople(event);
			addLinks(event);
			checkBookmark(event);
		}

		return events;
	}

	// TODO: verificare che funzioni xD
	@Override
	public List<Event> searchEventsByPerson(Person person) throws SQLException
	{
		QueryBuilder<Event, Long> queryBuilder = eventDao.queryBuilder();
		QueryBuilder<PersonPresentsEvent, Long> qb = personPresentsEventDao.queryBuilder();
		qb.where().eq("person_id", person.getId());
		queryBuilder.join(qb);

		List<Event> events = eventDao.query(queryBuilder.prepare());
		for(Event event : events) {
			addPeople(event);
			addLinks(event);
			checkBookmark(event);
		}

		return events;
	}

	@Override
	public List<Event> search(String query)
	{
		// TODO implementare
//		for(Event event : events) {
//			addPeople(event);
//			addLinks(event);
//			checkBookmark(event);
//		}
		return Collections.emptyList();
	}
}
