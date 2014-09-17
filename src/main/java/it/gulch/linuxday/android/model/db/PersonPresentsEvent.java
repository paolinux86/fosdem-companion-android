package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "person_present_event")
public class PersonPresentsEvent implements Serializable
{
	// TODO: remove when ormlite supports composite primary keys
	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(uniqueIndexName = "unique_person_event_ids", foreign = true, canBeNull = false)
	private Person person;

	@DatabaseField(uniqueIndexName = "unique_person_event_ids", foreign = true, canBeNull = false)
	private Event event;

	public PersonPresentsEvent()
	{
	}

	public PersonPresentsEvent(PersonPresentsEvent other)
	{
		this.id = other.id;
		this.person = other.person;
		this.event = other.event;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public Event getEvent()
	{
		return event;
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}
}
