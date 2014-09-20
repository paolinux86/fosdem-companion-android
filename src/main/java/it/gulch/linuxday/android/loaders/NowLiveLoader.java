package it.gulch.linuxday.android.loaders;

import android.content.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.enums.DatabaseOrder;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 19/09/14.
 */
public class NowLiveLoader extends BaseLiveLoader
{
	private final EventManager eventManager;

	public NowLiveLoader(Context context, EventManager eventManager)
	{
		super(context);
		this.eventManager = eventManager;
	}

	@Override
	protected List<Event> getObject()
	{
		Date now = GregorianCalendar.getInstance().getTime();

		try {
			return eventManager.search(null, now, now, DatabaseOrder.DESCENDING);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}
