package it.gulch.linuxday.android.db.manager.impl;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.OrmLiteDatabaseHelper;
import it.gulch.linuxday.android.db.manager.PersonManager;
import it.gulch.linuxday.android.model.db.Person;

/**
 * Created by paolo on 07/09/14.
 */
public class PersonManagerImpl implements PersonManager
{
	private static final String TAG = PersonManagerImpl.class.getSimpleName();

	private Dao<Person, Long> dao;

	private PersonManagerImpl()
	{
	}

	public static PersonManager newInstance(OrmLiteDatabaseHelper helper) throws SQLException
	{
		PersonManagerImpl personManager = new PersonManagerImpl();
		personManager.dao = helper.getDao(Person.class);

		return personManager;
	}

	@Override
	public Person get(Long id)
	{
		try {
			return dao.queryForId(id);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Person> getAll()
	{
		try {
			return dao.queryForAll();
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public void save(Person object) throws SQLException
	{
		dao.create(object);
	}

	@Override
	public void saveOrUpdate(Person object) throws SQLException
	{
		dao.createOrUpdate(object);
	}

	@Override
	public void update(Person object) throws SQLException
	{
		dao.update(object);
	}

	@Override
	public void delete(Person object) throws SQLException
	{
		dao.delete(object);
	}

	@Override
	public void truncate() throws SQLException
	{
		PreparedDelete<Person> preparedDelete = dao.deleteBuilder().prepare();
		dao.delete(preparedDelete);
	}

	@Override
	public boolean exists(Long objectId) throws SQLException
	{
		return dao.idExists(objectId);
	}
}
