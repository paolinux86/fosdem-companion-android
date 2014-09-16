package it.gulch.linuxday.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import it.gulch.linuxday.android.model.db.Track;

/**
 * Created by paolo on 14/09/14.
 */
public class TracksAdapter extends BaseAdapter
{
	private final LayoutInflater inflater;

	private List<Track> tracks;

	public TracksAdapter(Context context, List<Track> tracks)
	{
		super();
		inflater = LayoutInflater.from(context);
		this.tracks = tracks;
	}

	@Override
	public int getCount()
	{
		if(tracks == null) {
			return 0;
		}

		return tracks.size();
	}

	@Override
	public Track getItem(int position)
	{
		if(tracks == null || tracks.size() < 1 || position > tracks.size()) {
			return null;
		}

		return tracks.get(position);
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
			convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
			viewHolder.room = (TextView) convertView.findViewById(android.R.id.text2);
			convertView.setTag(viewHolder);
		}

		Track track = getItem(position);
		bindView(viewHolder, track);

		return convertView;
	}

	public void bindView(ViewHolder viewHolder, Track track)
	{
		viewHolder.track = track;
		viewHolder.name.setText(viewHolder.track.getTitle());
		viewHolder.room.setText(viewHolder.track.getRoom().getName());
	}

	private static class ViewHolder
	{
		TextView name;

		TextView room;

		Track track;
	}
}