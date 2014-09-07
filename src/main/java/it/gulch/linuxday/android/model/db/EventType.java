package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paolo on 06/09/14.
 */
@DatabaseTable(tableName = "event_type")
public class EventType
{
	@DatabaseField(id = true)
	private String code;

	@DatabaseField(canBeNull = false)
	private String description;

	@DatabaseField(canBeNull = true, dataType = DataType.BYTE_ARRAY)
	private byte[] image;

	public EventType()
	{
	}

	public EventType(EventType other)
	{
		this.code = other.code;
		this.description = other.description;
		this.image = other.image;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public byte[] getImage()
	{
		return image;
	}

	public void setImage(byte[] image)
	{
		this.image = image;
	}
}
