package it.gulch.linuxday.android.db.manager;

import java.util.List;

import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 07/09/14.
 */
public interface DayManager extends BaseORMManager<Day, Long>
{
	List<Day> getCachedDays();
}
