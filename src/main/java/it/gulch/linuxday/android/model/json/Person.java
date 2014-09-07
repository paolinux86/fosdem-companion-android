package it.gulch.linuxday.android.model.json;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
public class Person implements Serializable
{
	private Long person_id;

	private String name;

	private String middle_name;

	private String surname;

	private String description;

	private byte[] photo;

	public Long getPerson_id()
	{
		return person_id;
	}

	public void setPerson_id(Long person_id)
	{
		this.person_id = person_id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getMiddle_name()
	{
		return middle_name;
	}

	public void setMiddle_name(String middle_name)
	{
		this.middle_name = middle_name;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}
}
