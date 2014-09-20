package it.gulch.linuxday.android.loaders;

import android.content.Context;
import android.os.Handler;

import org.apache.commons.collections4.CollectionUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.db.manager.EventManager;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 19/09/14.
 */
public class BookmarksLoader extends SimpleDatabaseLoader<List<Event>>
{
	// Events that just started are still shown for 5 minutes
	private static final long TIME_OFFSET = 5L * 60L * 1000L;

	private static final int TIME_OFFSET_IN_MINUTES = 5;

	private final boolean upcomingOnly;

	private final Handler handler;

	private final EventManager eventManager;

	private final Runnable timeoutRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			onContentChanged();
		}
	};

	public BookmarksLoader(Context context, EventManager eventManager, boolean upcomingOnly)
	{
		super(context);
		this.upcomingOnly = upcomingOnly;
		this.handler = new Handler();
		this.eventManager = eventManager;
	}

	@Override
	public void deliverResult(List<Event> events)
	{
		if(upcomingOnly && !isReset()) {
			preDeliverResult(events);
		}

		super.deliverResult(events);
	}

	private void preDeliverResult(List<Event> events)
	{
		handler.removeCallbacks(timeoutRunnable);
		// The loader will be refreshed when the start time of the first bookmark in the list is reached
		if(CollectionUtils.isEmpty(events)) {
			return;
		}

		Event firstEvent = events.get(0);
		long startTime = firstEvent.getStartDate().getTime();
		if(startTime != -1L) {
			long delay = startTime - (System.currentTimeMillis() - TIME_OFFSET);
			if(delay > 0L) {
				handler.postDelayed(timeoutRunnable, delay);
			} else {
				onContentChanged();
			}
		}
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		if(upcomingOnly) {
			handler.removeCallbacks(timeoutRunnable);
		}
	}

	@Override
	protected List<Event> getObject()
	{
		Date minStartDate = null;
		if(upcomingOnly) {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.add(Calendar.MINUTE, -TIME_OFFSET_IN_MINUTES);
			minStartDate = calendar.getTime();
		}

		try {
			return eventManager.getBookmarkedEvents(minStartDate);
		} catch(SQLException e) {
			return Collections.emptyList();
		}
	}
}