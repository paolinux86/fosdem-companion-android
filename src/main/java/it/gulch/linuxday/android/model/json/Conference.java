package it.gulch.linuxday.android.model.json;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paolo on 06/09/14.
 */
public class Conference implements Serializable
{
	private Long conference_id;

	private String title;

	private String subtitle;

	private Address venue;

	private Calendar start_date;

	private Calendar end_date;

	private List<Day> days;

	public Long getConference_id()
	{
		return conference_id;
	}

	public void setConference_id(Long conference_id)
	{
		this.conference_id = conference_id;
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

	public Address getVenue()
	{
		return venue;
	}

	public void setVenue(Address address)
	{
		this.venue = address;
	}

	public Calendar getStart_date()
	{
		return start_date;
	}

	public void setStart_date(Calendar start_date)
	{
		this.start_date = start_date;
	}

	public Calendar getEnd_date()
	{
		return end_date;
	}

	public void setEnd_date(Calendar end_date)
	{
		this.end_date = end_date;
	}

	public List<Day> getDays()
	{
		return days;
	}

	public void setDays(List<Day> days)
	{
		this.days = days;
	}
}
