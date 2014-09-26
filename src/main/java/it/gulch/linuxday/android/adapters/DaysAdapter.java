package it.gulch.linuxday.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import it.gulch.linuxday.android.fragments.TracksListFragment;
import it.gulch.linuxday.android.model.db.Day;

/**
 * Created by paolo on 26/09/14.
 */
public class DaysAdapter extends FragmentStatePagerAdapter
{
	private List<Day> days;

	public DaysAdapter(FragmentManager fm, List<Day> days)
	{
		super(fm);
		this.days = days;
	}

	@Override
	public int getCount()
	{
		return (days == null) ? 0 : days.size();
	}

	@Override
	public Fragment getItem(int position)
	{
		return TracksListFragment.newInstance(days.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return days.get(position).toString();
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