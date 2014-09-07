package it.gulch.linuxday.android.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Day;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.EventType;
import it.gulch.linuxday.android.model.db.Link;
import it.gulch.linuxday.android.model.db.Person;
import it.gulch.linuxday.android.model.db.PersonPresentsEvent;
import it.gulch.linuxday.android.model.db.Room;
import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 07/09/14.
 */
public class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper
{
	private static final String DATABASE_NAME = "linuxdayca.db";

	private static final int DATABASE_VERSION = 1;

	private static final String TAG = OrmLiteDatabaseHelper.class.getSimpleName();

	public OrmLiteDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource)
	{
		Log.i(TAG, "onCreate");

		try {
			TableUtils.createTable(connectionSource, Day.class);
			TableUtils.createTable(connectionSource, Room.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Track.class);
			TableUtils.createTable(connectionSource, EventType.class);
			TableUtils.createTable(connectionSource, Event.class);
			TableUtils.createTable(connectionSource, Link.class);
			TableUtils.createTable(connectionSource, PersonPresentsEvent.class);
			TableUtils.createTable(connectionSource, Bookmark.class);
		} catch(java.sql.SQLException e) {
			Log.e(TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}

		Log.i(TAG, "Database created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
						  int newVersion)
	{
	}
}
