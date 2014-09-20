package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.util.List;

import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.model.db.Person;

/**
 * Created by paolo on 19/09/14.
 */
public class PeopleLoader extends SimpleDatabaseLoader<List<Person>>
{
	private PersonManager personManager;

	public PeopleLoader(Context context, PersonManager personManager)
	{
		super(context);
		this.personManager = personManager;
	}

	@Override
	protected List<Person> getObject()
	{
		return personManager.getAll();
	}
}