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
package it.gulch.linuxday.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.activities.PersonInfoActivity;
import it.gulch.linuxday.android.db.manager.BookmarkManager;
import it.gulch.linuxday.android.db.manager.impl.BookmarkManagerImpl;
import it.gulch.linuxday.android.db.manager.impl.DatabaseManagerFactory;
import it.gulch.linuxday.android.loaders.BookmarkStatusLoader;
import it.gulch.linuxday.android.loaders.LocalCacheLoader;
import it.gulch.linuxday.android.model.db.Event;
import it.gulch.linuxday.android.model.db.Link;
import it.gulch.linuxday.android.model.db.Person;
import it.gulch.linuxday.android.utils.DateUtils;

public class EventDetailsFragment extends Fragment
{
	private static final int BOOKMARK_STATUS_LOADER_ID = 1;

	private static final int EVENT_DETAILS_LOADER_ID = 2;

	private static final String ARG_EVENT = "event";

	private static final DateFormat TIME_DATE_FORMAT = DateUtils.getTimeDateFormat();

	private static final String TAG = EventDetailsFragment.class.getSimpleName();

	private Event event;

	private int personsCount = 1;

	private Boolean isBookmarked;

	private ViewHolder holder;

	private MenuItem bookmarkMenuItem;

	private BookmarkManager bookmarkManager;

	public static EventDetailsFragment newInstance(Event event)
	{
		EventDetailsFragment f = new EventDetailsFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_EVENT, event);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		event = (Event) getArguments().getSerializable(ARG_EVENT);
		setHasOptionsMenu(true);
	}

	public Event getEvent()
	{
		return event;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_event_details, container, false);

		holder = new ViewHolder();
		holder.inflater = inflater;

		((TextView) view.findViewById(R.id.title)).setText(event.getTitle());
		TextView textView = (TextView) view.findViewById(R.id.subtitle);
		String text = event.getSubtitle();
		if(TextUtils.isEmpty(text)) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(text);
		}

		MovementMethod linkMovementMethod = LinkMovementMethod.getInstance();

		// Set the persons summary text first; replace it with the clickable text when the loader completes
		holder.personsTextView = (TextView) view.findViewById(R.id.persons);
		String personsSummary = "";
		if(CollectionUtils.isEmpty(event.getPeople())) {
			holder.personsTextView.setVisibility(View.GONE);
		} else {
			personsSummary = StringUtils.join(event.getPeople(), ", ");
			holder.personsTextView.setText(personsSummary);
			holder.personsTextView.setMovementMethod(linkMovementMethod);
			holder.personsTextView.setVisibility(View.VISIBLE);
		}

		((TextView) view.findViewById(R.id.track)).setText(event.getTrack().getTitle());
		Date startTime = event.getStartDate();
		Date endTime = event.getEndDate();
		text = String.format("%1$s, %2$s ― %3$s", event.getTrack().getDay().getName(),
							 (startTime != null) ? TIME_DATE_FORMAT.format(startTime) : "?",
							 (endTime != null) ? TIME_DATE_FORMAT.format(endTime) : "?");
		((TextView) view.findViewById(R.id.time)).setText(text);
		final String roomName = event.getTrack().getRoom().getName();
		TextView roomTextView = (TextView) view.findViewById(R.id.room);
		Spannable roomText = new SpannableString(String.format("%1$s", roomName));
		//		final int roomImageResId = getResources()
		//				.getIdentifier(StringUtils.roomNameToResourceName(roomName), "drawable",
		//							   getActivity().getPackageName());
		//		// If the room image exists, make the room text clickable to display it
		//		if(roomImageResId != 0) {
		//			roomText.setSpan(new UnderlineSpan(), 0, roomText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//			roomTextView.setOnClickListener(new View.OnClickListener()
		//			{
		//
		//				@Override
		//				public void onClick(View view)
		//				{
		//					RoomImageDialogFragment.newInstance(roomName, roomImageResId).show(getFragmentManager());
		//				}
		//			});
		//			roomTextView.setFocusable(true);
		//		}
		roomTextView.setText(roomText);

		textView = (TextView) view.findViewById(R.id.abstract_text);
		text = event.getEventAbstract();
		if(TextUtils.isEmpty(text)) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(StringUtils.stripEnd(Html.fromHtml(text).toString(), " "));
			textView.setMovementMethod(linkMovementMethod);
		}
		textView = (TextView) view.findViewById(R.id.description);
		text = event.getDescription();
		if(TextUtils.isEmpty(text)) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(StringUtils.stripEnd(Html.fromHtml(text).toString(), " "));
			textView.setMovementMethod(linkMovementMethod);
		}

		holder.linksContainer = (ViewGroup) view.findViewById(R.id.links_container);
		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		holder = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(BOOKMARK_STATUS_LOADER_ID, null, bookmarkStatusLoaderCallbacks);
		loaderManager.initLoader(EVENT_DETAILS_LOADER_ID, null, eventDetailsLoaderCallbacks);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		setupServices(activity);
	}

	private void setupServices(Activity activity)
	{
		try {
			bookmarkManager = DatabaseManagerFactory.getBookmarkManager(activity);
		} catch(SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.event, menu);
		menu.findItem(R.id.share).setIntent(getShareChooserIntent());
		bookmarkMenuItem = menu.findItem(R.id.bookmark);
		updateOptionsMenu();
	}

	// FIXME: ricontrollare
	private Intent getShareChooserIntent()
	{
		return ShareCompat.IntentBuilder.from(getActivity())
				.setSubject(String.format("%1$s (FOSDEM)", event.getTitle())).setType("text/plain")
						//.setText(String.format("%1$s %2$s #FOSDEM", event.getTitle(), event.getUrl()))
				.setText(String.format("%1$s #FOSDEM", event.getTitle())).setChooserTitle(R.string.share)
				.createChooserIntent();
	}

	private void updateOptionsMenu()
	{
		if(bookmarkMenuItem != null) {
			if(isBookmarked == null) {
				bookmarkMenuItem.setEnabled(false);
			} else {
				bookmarkMenuItem.setEnabled(true);

				if(isBookmarked) {
					bookmarkMenuItem.setTitle(R.string.remove_bookmark);
					bookmarkMenuItem.setIcon(R.drawable.ic_action_important);
				} else {
					bookmarkMenuItem.setTitle(R.string.add_bookmark);
					bookmarkMenuItem.setIcon(R.drawable.ic_action_not_important);
				}
			}
		}
	}

	@Override
	public void onDestroyOptionsMenu()
	{
		super.onDestroyOptionsMenu();
		bookmarkMenuItem = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.bookmark:
				if(isBookmarked != null) {
					new UpdateBookmarkAsyncTask(event).execute(isBookmarked);
				}
				return true;
			case R.id.add_to_agenda:
				addToAgenda();
				return true;
		}
		return false;
	}

	private class UpdateBookmarkAsyncTask extends AsyncTask<Boolean, Void, Void>
	{
		private final String TAG = UpdateBookmarkAsyncTask.class.getSimpleName();

		private final Event event;

		public UpdateBookmarkAsyncTask(Event event)
		{
			this.event = event;
		}

		@Override
		protected Void doInBackground(Boolean... remove)
		{
			try {
				if(remove[0]) {
					bookmarkManager.removeBookmark(event);
				} else {
					bookmarkManager.addBookmark(event);
				}
				isBookmarked = !isBookmarked;
				getActivity().supportInvalidateOptionsMenu();
			} catch(SQLException e) {
				Log.e(TAG, e.getMessage(), e);
			}

			return null;
		}
	}

	@SuppressLint("InlinedApi")
	private void addToAgenda()
	{
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
		intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "ULB - " + event.getTrack().getRoom().getName());
		String description = event.getEventAbstract();
		if(TextUtils.isEmpty(description)) {
			description = event.getDescription();
		}
		// Strip HTML
		description = StringUtils.stripEnd(Html.fromHtml(description).toString(), " ");
		// Add speaker info if available
		if(personsCount > 0) {
			String personsSummary = StringUtils.join(event.getPeople(), ", ");
			description = String.format("%1$s: %2$s\n\n%3$s",
										getResources().getQuantityString(R.plurals.speakers, personsCount),
										personsSummary, description);
		}
		intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
		Date time = event.getStartDate();
		if(time != null) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.getTime());
		}
		time = event.getEndDate();
		if(time != null) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.getTime());
		}
		startActivity(intent);
	}

	private final LoaderCallbacks<Boolean> bookmarkStatusLoaderCallbacks = new LoaderCallbacks<Boolean>()
	{
		@Override
		public Loader<Boolean> onCreateLoader(int id, Bundle args)
		{
			return new BookmarkStatusLoader(getActivity(), event);
		}

		@Override
		public void onLoadFinished(Loader<Boolean> loader, Boolean data)
		{
			isBookmarked = data;
			updateOptionsMenu();
		}

		@Override
		public void onLoaderReset(Loader<Boolean> loader)
		{
		}
	};

	private static class EventDetailsLoader extends LocalCacheLoader<EventDetails>
	{
		private final Event event;

		public EventDetailsLoader(Context context, Event event)
		{
			super(context);
			this.event = event;
		}

		@Override
		public EventDetails loadInBackground()
		{
			EventDetails result = new EventDetails();
			result.people = event.getPeople();
			result.links = event.getLinks();
			return result;
		}
	}

	private final LoaderCallbacks<EventDetails> eventDetailsLoaderCallbacks = new LoaderCallbacks<EventDetails>()
	{
		@Override
		public Loader<EventDetails> onCreateLoader(int id, Bundle args)
		{
			return new EventDetailsLoader(getActivity(), event);
		}

		@Override
		public void onLoadFinished(Loader<EventDetails> loader, EventDetails data)
		{
			// 1. Persons
			if(data.people != null) {
				personsCount = data.people.size();
				if(personsCount > 0) {
					// Build a list of clickable persons
					SpannableStringBuilder sb = new SpannableStringBuilder();
					int length = 0;
					for(Person person : data.people) {
						if(length != 0) {
							sb.append(", ");
						}
						String name = person.getCompleteName(Person.CompleteNameEnum.NAME_FIRST);
						sb.append(name);
						length = sb.length();
						sb.setSpan(new PersonClickableSpan(person), length - name.length(), length,
								   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					holder.personsTextView.setText(sb);
					holder.personsTextView.setVisibility(View.VISIBLE);
				}
			}

			// 2. Links
			// Keep the first 2 views in links container (titles) only
			int linkViewCount = holder.linksContainer.getChildCount();
			if(linkViewCount > 2) {
				holder.linksContainer.removeViews(2, linkViewCount - 2);
			}
			if((data.links != null) && (data.links.size() > 0)) {
				holder.linksContainer.setVisibility(View.VISIBLE);
				for(Link link : data.links) {
					View view = holder.inflater.inflate(R.layout.item_link, holder.linksContainer, false);
					TextView tv = (TextView) view.findViewById(R.id.description);
					tv.setText(link.getDescription());
					//view.setOnClickListener(new LinkClickListener(link));
					holder.linksContainer.addView(view);
					// Add a list divider
					holder.inflater.inflate(R.layout.list_divider, holder.linksContainer, true);
				}
			} else {
				holder.linksContainer.setVisibility(View.GONE);
			}
		}

		@Override
		public void onLoaderReset(Loader<EventDetails> loader)
		{
		}
	};

	private static class PersonClickableSpan extends ClickableSpan
	{
		private final Person person;

		public PersonClickableSpan(Person person)
		{
			this.person = person;
		}

		@Override
		public void onClick(View v)
		{
			Context context = v.getContext();
			Intent intent =
					new Intent(context, PersonInfoActivity.class).putExtra(PersonInfoActivity.EXTRA_PERSON, person);
			context.startActivity(intent);
		}
	}

	// TODO
//	private static class LinkClickListener implements View.OnClickListener
//	{
//		private final Link link;
//
//		public LinkClickListener(Link link)
//		{
//			this.link = link;
//		}
//
//		@Override
//		public void onClick(View v)
//		{
//			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
//			v.getContext().startActivity(intent);
//		}
//	}

	private static class EventDetails
	{
		List<Person> people;

		List<Link> links;
	}

	private static class ViewHolder
	{
		LayoutInflater inflater;

		TextView personsTextView;

		ViewGroup linksContainer;
	}
}
