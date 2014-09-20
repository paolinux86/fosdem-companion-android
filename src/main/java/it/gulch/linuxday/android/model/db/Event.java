package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "event")
public class Event implements Serializable
{
	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(canBeNull = false, index = true, dataType = DataType.DATE_LONG)
	private Date startDate;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG)
	private Date endDate;

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

	@DatabaseField(canBeNull = false, foreign = true, index = true, foreignAutoRefresh = true)
	private Track track;

	private Boolean bookmarked;

	private List<Person> people;

	private List<Link> links;

	public Event()
	{
	}

	public Event(it.gulch.linuxday.android.model.json.Event event)
	{
		this.id = event.getId();
		this.title = event.getTitle();
		this.subtitle = event.getSubtitle();
		this.eventAbstract = event.getEventAbstract();
		this.description = event.getDescription();

		this.startDate = event.getStartDate().getTime();
		Calendar endTimeCalendar = (Calendar) event.getStartDate().clone();
		endTimeCalendar.add(Calendar.MINUTE, event.getDuration());

		this.endDate = endTimeCalendar.getTime();
	}

	public Event(Event other)
	{
		this.id = other.id;
		this.startDate = other.startDate;
		this.endDate = other.endDate;
		this.title = other.title;
		this.subtitle = other.subtitle;
		this.eventAbstract = other.eventAbstract;
		this.description = other.description;
		this.eventType = other.eventType;
		this.track = other.track;
		this.people = other.people;
		this.links = other.links;
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

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
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

	public void addPerson(Person person)
	{
		if(people == null) {
			people = new ArrayList<Person>();
		}

		people.add(person);
	}

	public List<Link> getLinks()
	{
		return links;
	}

	public void setLinks(List<Link> links)
	{
		this.links = links;
	}

	public Boolean isBookmarked()
	{
		return bookmarked;
	}

	public void setBookmarked(Boolean bookmarked)
	{
		this.bookmarked = bookmarked;
	}

	public boolean isRunningAtTime(long time)
	{
		return (startDate != null) && (endDate != null) && (startDate.getTime() < time) && (time < endDate.getTime());
	}
}
