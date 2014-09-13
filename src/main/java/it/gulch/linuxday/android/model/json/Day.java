package it.gulch.linuxday.android.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paolo on 06/09/14.
 */
public class Day implements Serializable
{
	@JsonProperty("day_id")
	private Long id;

	private String name;

	@JsonProperty("day_date")
	private Calendar dayDate;

	private List<Track> tracks;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Calendar getDayDate()
	{
		return dayDate;
	}

	public void setDayDate(Calendar dayDate)
	{
		this.dayDate = dayDate;
	}

	public List<Track> getTracks()
	{
		return tracks;
	}

	public void setTracks(List<Track> tracks)
	{
		this.tracks = tracks;
	}

	public it.gulch.linuxday.android.model.db.Day toDatabaseDay()
	{
		it.gulch.linuxday.android.model.db.Day day = new it.gulch.linuxday.android.model.db.Day();
		day.setId(id);
		day.setName(name);
		day.setDayDate(dayDate.getTime());

		return day;
	}
}
