package it.gulch.linuxday.android.db.manager.impl;

import com.j256.ormlite.dao.Dao;

import it.gulch.linuxday.android.db.manager.BaseORMManager;

/**
 * Created by paolo on 07/09/14.
 */
public abstract class BaseORMManagerImpl<K, T> implements BaseORMManager<K, T>
{
	protected Dao<K, T> dao;
}
