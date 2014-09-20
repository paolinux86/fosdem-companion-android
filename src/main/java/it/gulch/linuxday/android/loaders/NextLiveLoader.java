package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 19/09/14.
 */
public class NextLiveLoader extends BaseLiveLoader
{
	private static final int INTERVAL_IN_MINUTES = 30;

	private final EventManager eventManager;

	public NextLiveLoader(Context context, EventManager eventManager)
	{
		super(context);
		this.eventManager = eventManager;
	}

	@Override
	protected List<Event> getObject()
	{
		Calendar minStart = GregorianCalendar.getInstance();
		Calendar maxStart = (Calendar) minStart.clone();
		maxStart.add(Calendar.MINUTE, INTERVAL_IN_MINUTES);

		try {
			return eventManager.search(minStart.getTime(), maxStart.getTime(), null, DatabaseOrder.ASCENDING);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}
