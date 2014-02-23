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

public class Link implements Parcelable
{
	private String url;

	private String description;

	public Link()
	{
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public String toString()
	{
		return description;
	}

	@Override
	public int hashCode()
	{
		return url.hashCode();
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
		Link other = (Link) obj;
		return url.equals(other.url);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(url);
		out.writeString(description);
	}

	public static final Parcelable.Creator<Link> CREATOR = new Parcelable.Creator<Link>()
	{
		public Link createFromParcel(Parcel in)
		{
			return new Link(in);
		}

		public Link[] newArray(int size)
		{
			return new Link[size];
		}
	};

	private Link(Parcel in)
	{
		url = in.readString();
		description = in.readString();
	}
}
