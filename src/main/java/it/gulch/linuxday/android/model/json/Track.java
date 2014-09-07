package it.gulch.linuxday.android.model.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by paolo on 06/09/14.
 */
public class Track implements Serializable
{
	private Long track_id;

	private String title;

	private String subtitle;

	private Room room;

	private List<Event> events;

	public Long getTrack_id()
	{
		return track_id;
	}

	public void setTrack_id(Long track_id)
	{
		this.track_id = track_id;
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

	public Room getRoom()
	{
		return room;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}

	public List<Event> getEvents()
	{
		return events;
	}

	public void setEvents(List<Event> events)
	{
		this.events = events;
	}
}
