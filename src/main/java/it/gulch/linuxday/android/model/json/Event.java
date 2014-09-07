package it.gulch.linuxday.android.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paolo on 06/09/14.
 */
public class Event implements Serializable
{
	private Long event_id;

	private Calendar start_date;

	private Integer duration;

	private String title;

	private String subtitle;

	@JsonProperty("abstract")
	private String event_abstract;

	private String description;

	@JsonProperty("event_type")
	private EventType eventType;

	private List<Person> person;

	private List<Link> links;

	public Long getEvent_id()
	{
		return event_id;
	}

	public void setEvent_id(Long event_id)
	{
		this.event_id = event_id;
	}

	public Calendar getStart_date()
	{
		return start_date;
	}

	public void setStart_date(Calendar start_date)
	{
		this.start_date = start_date;
	}

	public Integer getDuration()
	{
		return duration;
	}

	public void setDuration(Integer duration)
	{
		this.duration = duration;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}

	public String getEvent_abstract()
	{
		return event_abstract;
	}

	public void setEvent_abstract(String event_abstract)
	{
		this.event_abstract = event_abstract;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public EventType getEventType()
	{
		return eventType;
	}

	public void setEventType(EventType eventType)
	{
		this.eventType = eventType;
	}

	public List<Person> getPerson()
	{
		return person;
	}

	public void setPerson(List<Person> person)
	{
		this.person = person;
	}

	public List<Link> getLinks()
	{
		return links;
	}

	public void setLinks(List<Link> links)
	{
		this.links = links;
	}
}
