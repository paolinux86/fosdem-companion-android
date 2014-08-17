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
package it.gulch.linuxday.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import it.gulch.linuxday.android.api.LinuxDayUrls;
import it.gulch.linuxday.android.db.DatabaseManager;
import it.gulch.linuxday.android.utils.StringUtils;

public class Person implements Parcelable
{
	private long id;

	private String name;

	public Person()
	{
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return LinuxDayUrls.getPerson(StringUtils.toSlug(name), DatabaseManager.getInstance().getYear());
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return (int) (id ^ (id >>> 32));
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		Person other = (Person) obj;
		return (id == other.id);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(id);
		out.writeString(name);
	}

	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>()
	{
		public Person createFromParcel(Parcel in)
		{
			return new Person(in);
		}

		public Person[] newArray(int size)
		{
			return new Person[size];
		}
	};

	private Person(Parcel in)
	{
		id = in.readLong();
		name = in.readString();
	}
}
