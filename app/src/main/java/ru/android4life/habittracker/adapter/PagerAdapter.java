package ru.android4life.habittracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.android4life.habittracker.fragment.HabitCardFragment;
import ru.android4life.habittracker.fragment.HabitStatisticsFragment;

/**
 * Adapter for tabs (basically, the habit card and statistics)
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private int habitScheduleId;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, int habitScheduleId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.habitScheduleId = habitScheduleId;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment tab1 = HabitCardFragment.newInstance(habitScheduleId);
                return tab1;
            case 1:
                Fragment tab2 = HabitStatisticsFragment.newInstance(habitScheduleId);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}