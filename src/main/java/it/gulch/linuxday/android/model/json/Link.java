package it.gulch.linuxday.android.model.json;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
public class Link implements Serializable
{
	private Long link_id;

	private String link;

	private String description;

	public Long getLink_id()
	{
		return link_id;
	}

	public void setLink_id(Long link_id)
	{
		this.link_id = link_id;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
