package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
@DatabaseTable(tableName = "link")
public class Link implements Serializable
{
	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(canBeNull = false)
	private String link;

	@DatabaseField(canBeNull = false)
	private String description;

	@DatabaseField(canBeNull = false, foreign = true)
	private Event event;

	public Link()
	{
	}

	public Link(it.gulch.linuxday.android.model.json.Link link)
	{
		this.id = link.getId();
		this.link = link.getLink();
		this.description = link.getDescription();
	}

	public Link(Link other)
	{
		this.id = other.id;
		this.link = other.link;
		this.description = other.description;
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

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
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
