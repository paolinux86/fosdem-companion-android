package it.gulch.linuxday.android.model.json;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paolo on 06/09/14.
 */
public class Day implements Serializable
{
	private Long day_id;

	private String name;

	private Calendar day_date;

	private List<Track> tracks;

	public Long getDay_id()
	{
		return day_id;
	}

	public void setDay_id(Long day_id)
	{
		this.day_id = day_id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Calendar getDay_date()
	{
		return day_date;
	}

	public void setDay_date(Calendar day_date)
	{
		this.day_date = day_date;
	}

	public List<Track> getTracks()
	{
		return tracks;
	}

	public void setTracks(List<Track> tracks)
	{
		this.tracks = tracks;
	}
}
