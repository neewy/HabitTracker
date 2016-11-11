package ru.android4life.habittracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.android4life.habittracker.fragment.HabitCardFragment;
import ru.android4life.habittracker.fragment.HabitNotesFragment;
import ru.android4life.habittracker.fragment.HabitStatisticsFragment;

/**
 * Adapter for tabs (basically, the habit card and statistics)
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private int habitId;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, int habitId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.habitId = habitId;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment tab1 = HabitCardFragment.newInstance(habitId);
                return tab1;
            case 1:
                Fragment tab2 = HabitStatisticsFragment.newInstance(habitId);
                return tab2;
            case 2:
                Fragment tab3 = HabitNotesFragment.newInstance(habitId);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}