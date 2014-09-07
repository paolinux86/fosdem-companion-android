package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "track")
public class Track
{
	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(canBeNull = false)
	private String title;

	@DatabaseField(canBeNull = true)
	private String subtitle;

	@DatabaseField(canBeNull = false, foreign = true)
	private Room room;

	@DatabaseField(canBeNull = false, foreign = true, index = true)
	private Day day;

	public Track()
	{
	}

	public Track(it.gulch.linuxday.android.model.json.Track track)
	{
		this.id = track.getId();
		this.title = track.getTitle();
		this.subtitle = track.getSubtitle();
		this.room = new Room(track.getRoom());
	}

	public Track(Track other)
	{
		this.id = other.id;
		this.title = other.title;
		this.subtitle = other.subtitle;
		this.room = other.room;
		this.day = other.day;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
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

	public Day getDay()
	{
		return day;
	}

	public void setDay(Day day)
	{
		this.day = day;
	}
}
