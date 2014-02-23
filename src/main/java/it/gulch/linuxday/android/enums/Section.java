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
package it.gulch.linuxday.android.enums;

import android.support.v4.app.Fragment;

import it.gulch.linuxday.android.R;
import it.gulch.linuxday.android.fragments.BookmarksListFragment;
import it.gulch.linuxday.android.fragments.LiveFragment;
import it.gulch.linuxday.android.fragments.MapFragment;
import it.gulch.linuxday.android.fragments.PersonsListFragment;
import it.gulch.linuxday.android.fragments.TracksFragment;

/**
 * Created by paolo on 23/02/14.
 */
public enum Section
{
	TRACKS(TracksFragment.class, R.string.menu_tracks, R.drawable.ic_action_event, true),
	BOOKMARKS(BookmarksListFragment.class, R.string.menu_bookmarks, R.drawable.ic_action_important, false),
	LIVE(LiveFragment.class, R.string.menu_live, R.drawable.ic_action_play_over_video, false),
	SPEAKERS(PersonsListFragment.class, R.string.menu_speakers, R.drawable.ic_action_group, false),
	MAP(MapFragment.class, R.string.menu_map, R.drawable.ic_action_map, false);

	private final String fragmentClassName;

	private final int titleResId;

	private final int iconResId;

	private final boolean keep;

	private Section(Class<? extends Fragment> fragmentClass, int titleResId, int iconResId, boolean keep)
	{
		this.fragmentClassName = fragmentClass.getName();
		this.titleResId = titleResId;
		this.iconResId = iconResId;
		this.keep = keep;
	}

	public String getFragmentClassName()
	{
		return fragmentClassName;
	}

	public int getTitleResId()
	{
		return titleResId;
	}

	public int getIconResId()
	{
		return iconResId;
	}

	public boolean shouldKeep()
	{
		return keep;
	}
}
