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
	@JsonProperty("event_id")
	private Long id;

	@JsonProperty("start_date")
	private Calendar startDate;

	private Integer duration;

	private String title;

	private String subtitle;

	@JsonProperty("abstract")
	private String eventAbstract;

	private String description;

	@JsonProperty("event_type")
	private EventType eventType;

	@JsonProperty("person")
	private List<Person> people;

	private List<Link> links;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Calendar getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Calendar startDate)
	{
		this.startDate = startDate;
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

	public String getEventAbstract()
	{
		return eventAbstract;
	}

	public void setEventAbstract(String eventAbstract)
	{
		this.eventAbstract = eventAbstract;
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

	public List<Person> getPeople()
	{
		return people;
	}

	public void setPeople(List<Person> people)
	{
		this.people = people;
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
