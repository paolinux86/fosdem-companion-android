package it.gulch.linuxday.android.db.manager;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by paolo on 07/09/14.
 */
public interface BaseORMManager<T, K>
{
	T get(K id);

	List<T> getAll();

	void save(T object) throws SQLException;

	void update(T object) throws SQLException;

	void delete(T object) throws SQLException;

	void truncate() throws SQLException;
}