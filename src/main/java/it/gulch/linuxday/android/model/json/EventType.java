package it.gulch.linuxday.android.model.json;

/**
 * Created by paolo on 06/09/14.
 */
public class EventType
{
	private String code;

	private String description;

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

	public it.gulch.linuxday.android.model.db.EventType toDatabaseEventType()
	{
		it.gulch.linuxday.android.model.db.EventType eventType = new it.gulch.linuxday.android.model.db.EventType();
		eventType.setCode(code);
		eventType.setDescription(description);

		return eventType;
	}
}
