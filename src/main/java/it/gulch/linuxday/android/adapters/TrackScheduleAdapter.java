package it.gulch.linuxday.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.utils.DateUtils;

/**
 * Created by paolo on 14/09/14.
 */
public class TrackScheduleAdapter extends BaseAdapter
{
	private static final DateFormat TIME_DATE_FORMAT = DateUtils.getTimeDateFormat();

	private final LayoutInflater inflater;

	private final int timeBackgroundColor;

	private final int timeForegroundColor;

	private final int timeRunningBackgroundColor;

	private final int timeRunningForegroundColor;

	private final int titleTextSize;

	private long currentTime = -1;

	private List<Event> events;

	public TrackScheduleAdapter(Context context, List<Event> events)
	{
		super();

		this.events = events;

		inflater = LayoutInflater.from(context);
		Resources res = context.getResources();
		timeBackgroundColor = res.getColor(R.color.schedule_time_background);
		timeForegroundColor = res.getColor(R.color.schedule_time_foreground);
		timeRunningBackgroundColor = res.getColor(R.color.schedule_time_running_background);
		timeRunningForegroundColor = res.getColor(R.color.schedule_time_running_foreground);
		titleTextSize = res.getDimensionPixelSize(R.dimen.list_item_title_text_size);
	}

	public void setCurrentTime(long time)
	{
		if(currentTime != time) {
			currentTime = time;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount()
	{
		if(events == null) {
			return 0;
		}

		return 0;
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
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if(convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.item_schedule_event, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			viewHolder.titleSizeSpan = new AbsoluteSizeSpan(titleTextSize);
			viewHolder.boldStyleSpan = new StyleSpan(Typeface.BOLD);
			convertView.setTag(viewHolder);
		}

		Event event = getItem(position);
		bindView(viewHolder, event);

		return convertView;
	}

	public void bindView(ViewHolder viewHolder, Event event)
	{
		viewHolder.event = event;

		viewHolder.time.setText(TIME_DATE_FORMAT.format(event.getStartDate()));
		if((currentTime != -1L) && event.isRunningAtTime(currentTime)) {
			// Contrast colors for running event
			viewHolder.time.setBackgroundColor(timeRunningBackgroundColor);
			viewHolder.time.setTextColor(timeRunningForegroundColor);
		} else {
			// Normal colors
			viewHolder.time.setBackgroundColor(timeBackgroundColor);
			viewHolder.time.setTextColor(timeForegroundColor);
		}

		String roomName = event.getTrack().getRoom().getName();
		SpannableString spannableString;
		String eventTitle = event.getTitle();

		String personsSummary = "";
		if(CollectionUtils.isEmpty(event.getPeople())) {
			spannableString = new SpannableString(String.format("%1$s\n%2$s", eventTitle, roomName));
		} else {
			personsSummary = StringUtils.join(event.getPeople(), ", ");
			spannableString = new SpannableString(
					String.format("%1$s\n%2$s\n%3$s", eventTitle, personsSummary, roomName));
		}
		spannableString.setSpan(viewHolder.titleSizeSpan, 0, eventTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(viewHolder.boldStyleSpan, 0, eventTitle.length() + personsSummary.length() + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		viewHolder.text.setText(spannableString);
		int bookmarkDrawable = event.isBookmarked() ? R.drawable.ic_small_starred : 0;
		viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, bookmarkDrawable, 0);
	}

	private static class ViewHolder
	{
		TextView time;

		TextView text;

		AbsoluteSizeSpan titleSizeSpan;

		StyleSpan boldStyleSpan;

		Event event;
	}
}