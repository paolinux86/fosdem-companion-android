package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "bookmark")
public class Bookmark implements Serializable
{
	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(canBeNull = false, unique = true, foreign = true, foreignAutoRefresh = true)
	private Event event;

	public Bookmark()
	{
	}

	public Bookmark(Bookmark other)
	{
		this.id = other.id;
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

	public Event getEvent()
	{
		return event;
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}
}
