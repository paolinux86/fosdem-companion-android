package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Person;

/**
 * Created by paolo on 18/09/14.
 */
public class PersonEventsLoader extends SimpleDatabaseLoader<List<Event>>
{
	private Person person;

	private EventManager eventManager;

	public PersonEventsLoader(Context context, EventManager eventManager, Person person)
	{
		super(context);
		this.person = person;
		this.eventManager = eventManager;
	}

	@Override
	protected List<Event> getObject()
	{
		try {
			return eventManager.searchEventsByPerson(person);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}
