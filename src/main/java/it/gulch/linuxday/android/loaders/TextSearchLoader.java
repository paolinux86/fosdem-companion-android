package it.gulch.linuxday.android.loaders;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 25/09/14.
 */
public class TextSearchLoader extends SimpleDatabaseLoader<List<Event>>
{
	private static final String TAG = TextSearchLoader.class.getSimpleName();

	private final String query;

	private EventManager eventManager;

	public TextSearchLoader(Context context, EventManager eventManager, String query)
	{
		super(context);
		this.query = query;
		this.eventManager = eventManager;
	}

	@Override
	protected List<Event> getObject()
	{
		try {
			return eventManager.search(query);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			return Collections.emptyList();
		}
	}
}