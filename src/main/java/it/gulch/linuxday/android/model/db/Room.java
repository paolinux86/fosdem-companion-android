package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "room")
public class Room
{
	@DatabaseField(id = true, canBeNull = false)
	private String name;

	public Room()
	{
	}

	public Room(it.gulch.linuxday.android.model.json.Room room)
	{
		this.name = room.getName();
	}

	public Room(Room other)
	{
		this.name = other.name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
