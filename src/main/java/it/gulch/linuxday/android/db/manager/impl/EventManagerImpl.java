package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Person;
import it.gulch.linuxday.android.model.db.PersonPresentsEvent;

/**
 * Created by paolo on 07/09/14.
 */
public class EventManagerImpl extends BaseORMManagerImpl<Event, Long> implements EventManager
{
	private static final String TAG = EventManagerImpl.class.getSimpleName();

	private Dao<PersonPresentsEvent, Long> personPresentsEventDao;

	@Override
	public Event get(Long id)
	{
		try {
			Event event = dao.queryForId(id);
			addPeople(event);

			return event;
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Event> getAll()
	{
		try {
			List<Event> events = dao.queryForAll();
			for(Event event : events) {
				addPeople(event);
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
		dao.create(object);

		insertPeopleInEvent(object);
	}

	private void insertPeopleInEvent(Event object) throws SQLException
	{
		for(Person person : object.getPeople()) {
			PersonPresentsEvent personPresentsEvent = new PersonPresentsEvent();
			personPresentsEvent.setEvent(object);
			personPresentsEvent.setPerson(person);

			personPresentsEventDao.create(personPresentsEvent);
		}
	}

	@Override
	public void saveOrUpdate(Event object) throws SQLException
	{
		Dao.CreateOrUpdateStatus status = dao.createOrUpdate(object);

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
		dao.update(object);
		removePeopleInEvent(object);
		insertPeopleInEvent(object);
	}

	@Override
	public void delete(Event object) throws SQLException
	{
		removePeopleInEvent(object);
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<PersonPresentsEvent> preparedDelete = personPresentsEventDao.deleteBuilder().prepare();
		personPresentsEventDao.delete(preparedDelete);

		PreparedDelete<Event> eventPreparedDelete = dao.deleteBuilder().prepare();
		dao.delete(eventPreparedDelete);
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
}
