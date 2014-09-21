package it.gulch.linuxday.android.db.manager;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import it.gulch.linuxday.android.model.db.Bookmark;
import it.gulch.linuxday.android.model.db.Event;

/**
 * Created by paolo on 07/09/14.
 */
public interface BookmarkManager extends BaseORMManager<Bookmark, Long>
{
	void deleteOldBookmarks(Long minEventId) throws SQLException;

	void addBookmark(Event event) throws SQLException;

	void removeBookmark(Event event) throws SQLException;

	void removeBookmarksByEventId(List<Long> eventIds) throws SQLException;

	List<Bookmark> getBookmarks(Date minStartTime) throws SQLException;
}
