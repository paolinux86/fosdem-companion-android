package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gulch.linuxday.android.model.json.Track;

/**
 * Created by paolo on 06/09/14.
 */
@DatabaseTable(tableName = "day")
public class Day
{
	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField(canBeNull = false)
	private Date dayDate;

	public Day()
	{
	}

	public Day(Day other)
	{
		this.id = other.id;
		this.name = other.name;
		this.dayDate = other.dayDate;
	}

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

	public Date getDayDate()
	{
		return dayDate;
	}

	public void setDayDate(Date dayDate)
	{
		this.dayDate = dayDate;
	}
}
