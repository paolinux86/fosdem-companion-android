/*
 * Copyright 2014 Christophe Beyls
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.gulch.linuxday.android.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.utils.DateUtils;

public class EventsAdapter extends BaseAdapter
{
	private static final DateFormat TIME_DATE_FORMAT = DateUtils.getTimeDateFormat();

	private final LayoutInflater inflater;

	private final int titleTextSize;

	private final boolean showDay;

	private List<Event> events;

	public EventsAdapter(Context context, List<Event> events)
	{
		this(context, events, true);
	}

	public EventsAdapter(Context context, List<Event> events, boolean showDay)
	{
		super();
		inflater = LayoutInflater.from(context);
		titleTextSize = context.getResources().getDimensionPixelSize(R.dimen.list_item_title_text_size);
		this.showDay = showDay;
		this.events = events;
	}

	@Override
	public Event getItem(int position)
	{
		if(events == null || events.size() < 1 || position > events.size()) {
			return null;
		}

		return events.get(position);
	}

	@Override
	public long getItemId(int i)
	{
		return events.get(i).getId();
	}

	@Override
	public int getCount()
	{
		if(events == null) {
			return 0;
		}

		return events.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if(convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.item_event, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.titleSizeSpan = new AbsoluteSizeSpan(titleTextSize);
			viewHolder.trackName = (TextView) convertView.findViewById(R.id.track_name);
			viewHolder.details = (TextView) convertView.findViewById(R.id.details);
			convertView.setTag(viewHolder);
		}

		Event event = getItem(position);
		bindView(viewHolder, event);

		return convertView;
	}

	private void bindView(ViewHolder viewHolder, Event event)
	{
		viewHolder.event = event;

		String eventTitle = event.getTitle();
		SpannableString spannableString;
		if(CollectionUtils.isEmpty(event.getPeople())) {
			spannableString = new SpannableString(eventTitle);
		} else {
			String personsSummary = StringUtils.join(event.getPeople(), ", ");
			spannableString = new SpannableString(String.format("%1$s\n%2$s", eventTitle, personsSummary));
		}

		spannableString.setSpan(viewHolder.titleSizeSpan, 0, eventTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		viewHolder.title.setText(spannableString);
		int bookmarkDrawable = event.isBookmarked() ? R.drawable.ic_small_starred : 0;
		viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, bookmarkDrawable, 0);

		viewHolder.trackName.setText(event.getTrack().getTitle());

		Date startTime = event.getStartDate();
		Date endTime = event.getEndDate();

		String startTimeString = (startTime != null) ? TIME_DATE_FORMAT.format(startTime) : "?";
		String endTimeString = (endTime != null) ? TIME_DATE_FORMAT.format(endTime) : "?";
		String details;

		String roomName = event.getTrack().getRoom().getName();
		if(showDay) {
			details = String.format("%1$s, %2$s ― %3$s  |  %4$s", event.getTrack().getDay().getName(), startTimeString,
									endTimeString, roomName);
		} else {
			details = String.format("%1$s ― %2$s  |  %3$s", startTimeString, endTimeString, roomName);
		}
		viewHolder.details.setText(details);
	}

	private static class ViewHolder
	{
		TextView title;

		AbsoluteSizeSpan titleSizeSpan;

		TextView trackName;

		TextView details;

		Event event;
	}
}
