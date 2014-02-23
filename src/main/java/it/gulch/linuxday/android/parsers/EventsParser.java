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
package it.gulch.linuxday.android.parsers;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.gulch.linuxday.android.model.Day;
import it.gulch.linuxday.android.model.Event;
import it.gulch.linuxday.android.model.Link;
import it.gulch.linuxday.android.model.Person;
import it.gulch.linuxday.android.model.Track;
import it.gulch.linuxday.android.utils.DateUtils;

/**
 * Main parser for FOSDEM schedule data in pentabarf XML format.
 *
 * @author Christophe Beyls
 */
public class EventsParser extends IterableAbstractPullParser<Event>
{
	private final DateFormat DATE_FORMAT = DateUtils.withBelgiumTimeZone(new SimpleDateFormat("yyyy-MM-dd", Locale.US));

	// Calendar used to compute the events time, according to Belgium timezone
	private final Calendar calendar = Calendar.getInstance(DateUtils.getBelgiumTimeZone(), Locale.US);

	private Day currentDay;

	private String currentRoom;

	private Track currentTrack;

	/**
	 * Returns the hours portion of a time string in the "hh:mm" format, without allocating objects.
	 *
	 * @param time
	 * @return hours
	 */
	private static int getHours(String time)
	{
		return (Character.getNumericValue(time.charAt(0)) * 10) + Character.getNumericValue(time.charAt(1));
	}

	/**
	 * Returns the minutes portion of a time string in the "hh:mm" format, without allocating objects.
	 *
	 * @param time
	 * @return minutes
	 */
	private static int getMinutes(String time)
	{
		return (Character.getNumericValue(time.charAt(3)) * 10) + Character.getNumericValue(time.charAt(4));
	}

	@Override
	protected boolean parseHeader(XmlPullParser parser) throws Exception
	{
		while(!isEndDocument()) {
			if(isStartTag("schedule")) {
				return true;
			}

			parser.next();
		}
		return false;
	}

	@Override
	protected Event parseNext(XmlPullParser parser) throws Exception
	{
		while(!isNextEndTag("schedule")) {
			if(isStartTag()) {
				String name = parser.getName();

				if("day".equals(name)) {
					currentDay = new Day();
					currentDay.setIndex(Integer.parseInt(parser.getAttributeValue(null, "index")));
					currentDay.setDate(DATE_FORMAT.parse(parser.getAttributeValue(null, "date")));
				} else if("room".equals(name)) {
					currentRoom = parser.getAttributeValue(null, "name");
				} else if("event".equals(name)) {
					Event event = new Event();
					event.setId(Long.parseLong(parser.getAttributeValue(null, "id")));
					event.setDay(currentDay);
					event.setRoomName(currentRoom);
					// Initialize empty lists
					List<Person> persons = new ArrayList<Person>();
					event.setPersons(persons);
					List<Link> links = new ArrayList<Link>();
					event.setLinks(links);

					String duration = null;
					String trackName = "";
					Track.Type trackType = Track.Type.other;

					while(!isNextEndTag("event")) {
						if(isStartTag()) {
							name = parser.getName();

							if("start".equals(name)) {
								String time = parser.nextText();
								if(!TextUtils.isEmpty(time)) {
									calendar.setTime(currentDay.getDate());
									calendar.set(Calendar.HOUR_OF_DAY, getHours(time));
									calendar.set(Calendar.MINUTE, getMinutes(time));
									event.setStartTime(calendar.getTime());
								}
							} else if("duration".equals(name)) {
								duration = parser.nextText();
							} else if("slug".equals(name)) {
								event.setSlug(parser.nextText());
							} else if("title".equals(name)) {
								event.setTitle(parser.nextText());
							} else if("subtitle".equals(name)) {
								event.setSubTitle(parser.nextText());
							} else if("track".equals(name)) {
								trackName = parser.nextText();
							} else if("type".equals(name)) {
								try {
									trackType = Enum.valueOf(Track.Type.class, parser.nextText());
								} catch(Exception e) {
									// trackType will be "other"
								}
							} else if("abstract".equals(name)) {
								event.setAbstractText(parser.nextText());
							} else if("description".equals(name)) {
								event.setDescription(parser.nextText());
							} else if("persons".equals(name)) {
								while(!isNextEndTag("persons")) {
									if(isStartTag("person")) {
										Person person = new Person();
										person.setId(Long.parseLong(parser.getAttributeValue(null, "id")));
										person.setName(parser.nextText());

										persons.add(person);
									}
								}
							} else if("links".equals(name)) {
								while(!isNextEndTag("links")) {
									if(isStartTag("link")) {
										Link link = new Link();
										link.setUrl(parser.getAttributeValue(null, "href"));
										link.setDescription(parser.nextText());

										links.add(link);
									}
								}
							} else {
								skipToEndTag();
							}
						}
					}

					if((event.getStartTime() != null) && !TextUtils.isEmpty(duration)) {
						calendar.add(Calendar.HOUR_OF_DAY, getHours(duration));
						calendar.add(Calendar.MINUTE, getMinutes(duration));
						event.setEndTime(calendar.getTime());
					}

					if((currentTrack == null) || !trackName.equals(currentTrack.getName()) ||
						(trackType != currentTrack.getType())) {
						currentTrack = new Track(trackName, trackType);
					}
					event.setTrack(currentTrack);

					return event;
				} else {
					skipToEndTag();
				}
			}
		}
		return null;
	}
}
