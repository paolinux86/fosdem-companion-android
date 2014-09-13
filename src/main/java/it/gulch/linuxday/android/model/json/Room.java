package it.gulch.linuxday.android.model.json;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
public class Room implements Serializable
{
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public it.gulch.linuxday.android.model.db.Room toDatabaseRoom()
	{
		it.gulch.linuxday.android.model.db.Room room = new it.gulch.linuxday.android.model.db.Room();
		room.setName(name);

		return room;
	}
}
