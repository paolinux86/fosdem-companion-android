package it.gulch.linuxday.android.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
public class Person implements Serializable
{
	@JsonProperty("person_id")
	private Long id;

	private String name;

	@JsonProperty("middle_name")
	private String middleName;

	private String surname;

	private String description;

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

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
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

	public it.gulch.linuxday.android.model.db.Person toDatabasePerson()
	{
		it.gulch.linuxday.android.model.db.Person person = new it.gulch.linuxday.android.model.db.Person();
		person.setId(id);
		person.setName(name);
		person.setMiddleName(middleName);
		person.setSurname(surname);
		person.setDescription(description);

		return person;
	}
}
