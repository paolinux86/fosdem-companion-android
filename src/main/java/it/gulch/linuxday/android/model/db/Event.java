package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "event")
public class Event
{
	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(canBeNull = false, index = true)
	private Date startDate;

	@DatabaseField(canBeNull = false)
	private Integer duration;

	@DatabaseField(canBeNull = false)
	private String title;

	@DatabaseField(canBeNull = true)
	private String subtitle;

	@DatabaseField(canBeNull = true)
	private String eventAbstract;

	@DatabaseField(canBeNull = true)
	private String description;

	@DatabaseField(canBeNull = false, foreign = true)
	private EventType eventType;

	@DatabaseField(canBeNull = false, foreign = true, index = true)
	private Track track;

	private List<Person> people;

	public Event()
	{
	}

	public Event(Event other)
	{
		this.id = other.id;
		this.startDate = other.startDate;
		this.duration = other.duration;
		this.title = other.title;
		this.subtitle = other.subtitle;
		this.eventAbstract = other.eventAbstract;
		this.description = other.description;
		this.eventType = other.eventType;
		this.track = other.track;
		this.people = other.people;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
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

	public Track getTrack()
	{
		return track;
	}

	public void setTrack(Track track)
	{
		this.track = track;
	}

	public List<Person> getPeople()
	{
		return people;
	}

	public void setPeople(List<Person> people)
	{
		this.people = people;
	}
}
