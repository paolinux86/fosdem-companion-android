package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by paolo on 06/09/14.
 */
@DatabaseTable(tableName = "day")
public class Day implements Serializable
{
	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG)
	private Date dayDate;

	public Day()
	{
	}

	public Day(it.gulch.linuxday.android.model.json.Day day)
	{
		this.id = day.getId();
		this.name = day.getName();
		this.dayDate = day.getDayDate().getTime();
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

	@Override
	public String toString()
	{
		return this.name;
	}
}
