package it.gulch.linuxday.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import it.gulch.linuxday.android.model.db.Person;

/**
 * Created by paolo on 14/09/14.
 */
public class PeopleAdapter extends BaseAdapter// implements SectionIndexer
{
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private final LayoutInflater inflater;

	// TODO
	//private final AlphabetIndexer indexer;

	private List<Person> people;

	public PeopleAdapter(Context context, List<Person> people)
	{
		super();
		inflater = LayoutInflater.from(context);
		//indexer = new AlphabetIndexer(null, DatabaseManager.PERSON_NAME_COLUMN_INDEX, ALPHABET);
		this.people = people;
	}

	@Override
	public int getCount()
	{
		if(people == null) {
			return 0;
		}

		return people.size();
	}

	@Override
	public Person getItem(int position)
	{
		return people.get(position);
	}

	@Override
	public long getItemId(int i)
	{
		if(people == null || people.size() < 1 || i > people.size()) {
			return 0;
		}

		return people.get(i).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if(convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(viewHolder);
		}

		Person person = getItem(position);
		bindView(viewHolder, person);

		return convertView;
	}

	public void bindView(ViewHolder viewHolder, Person person)
	{
		viewHolder.person = person;
		viewHolder.textView.setText(viewHolder.person.getName());
	}

//	@Override
//	public Cursor swapCursor(Cursor newCursor)
//	{
//		indexer.setCursor(newCursor);
//		return super.swapCursor(newCursor);
//	}
//
//	@Override
//	public int getPositionForSection(int sectionIndex)
//	{
//		return indexer.getPositionForSection(sectionIndex);
//	}
//
//	@Override
//	public int getSectionForPosition(int position)
//	{
//		return indexer.getSectionForPosition(position);
//	}
//
//	@Override
//	public Object[] getSections()
//	{
//		return indexer.getSections();
//	}

	private static class ViewHolder
	{
		public TextView textView;

		public Person person;
	}
}