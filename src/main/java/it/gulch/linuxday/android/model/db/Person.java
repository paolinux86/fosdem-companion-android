package it.gulch.linuxday.android.model.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Created by paolo on 07/09/14.
 */
@DatabaseTable(tableName = "person")
public class Person implements Serializable
{
	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(canBeNull = false)
	private String name;

	@DatabaseField(canBeNull = true)
	private String middleName;

	@DatabaseField(canBeNull = false)
	private String surname;

	@DatabaseField(canBeNull = false)
	private String description;

	@DatabaseField(canBeNull = true, dataType = DataType.BYTE_ARRAY)
	private byte[] photo;

	public Person()
	{
	}

	public Person(it.gulch.linuxday.android.model.json.Person person)
	{
		this.id = person.getId();
		this.name = person.getName();
		this.middleName = person.getMiddleName();
		this.surname = person.getSurname();
		this.description = person.getDescription();
		this.photo = null;
	}

	public Person(Person other)
	{
		this.id = other.id;
		this.name = other.name;
		this.middleName = other.middleName;
		this.surname = other.surname;
		this.description = other.description;
		this.photo = other.photo;
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

	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}

	@Override
	public String toString()
	{
		if(middleName == null) {
			return MessageFormat.format("{0} {1}", name, surname);
		}
		return MessageFormat.format("{0} {1} {2}", name, middleName, surname);
	}

	public String getCompleteName(CompleteNameEnum completeNameEnum)
	{
		String format = "{0} {2} {1}";

		if(middleName == null) {
			format = "{0} {1}";
		}

		if(completeNameEnum == CompleteNameEnum.NAME_FIRST) {
			return MessageFormat.format(format, name, surname, middleName);
		} else {
			return MessageFormat.format(format, surname, name, middleName);
		}
	}

	public enum CompleteNameEnum
	{
		NAME_FIRST,
		SURNAME_FIRST
	}
}
