package it.gulch.linuxday.android.model.json;

/**
 * Created by paolo on 06/09/14.
 */
public class EventType
{
	private String code;

	private String description;

	private byte[] image;

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
