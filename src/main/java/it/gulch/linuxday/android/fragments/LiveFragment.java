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

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import it.gulch.linuxday.android.R;

public class LiveFragment extends Fragment
{
	private LivePagerAdapter livePagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		livePagerAdapter = new LivePagerAdapter(getChildFragmentManager(), getResources());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_live, container, false);

		ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		pager.setAdapter(livePagerAdapter);
		PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		return view;
	}

	private static class LivePagerAdapter extends FragmentPagerAdapter
	{

		private final Resources resources;

		public LivePagerAdapter(FragmentManager fm, Resources resources)
		{
			super(fm);
			this.resources = resources;
		}

		@Override
		public int getCount()
		{
			return 2;
		}

		@Override
		public Fragment getItem(int position)
		{
			switch(position) {
				case 0:
					return new NextLiveListFragment();
				case 1:
					return new NowLiveListFragment();
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			switch(position) {
				case 0:
					return resources.getString(R.string.next);
				case 1:
					return resources.getString(R.string.now);
			}
			return null;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object)
		{
			super.setPrimaryItem(container, position, object);
			// Hack to allow the non-primary fragments to start properly
			if(object != null) {
				((Fragment) object).setUserVisibleHint(false);
			}
		}
	}
}