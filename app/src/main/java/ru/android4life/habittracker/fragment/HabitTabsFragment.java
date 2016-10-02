package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.android4life.habittracker.R;

/**
 * Tabs controller class.
 * Layout becomes visible,
 * when the card is opened from the list of habits.
 * <p>
 * Created by Nikolay Yushkevich on 27.09.16.
 */
public class HabitTabsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View tabsView = inflater.inflate(R.layout.habit_tabs, container, false);

        // Strip with tabs, which goes under the action bar.
        TabLayout tabLayout = (TabLayout) tabsView.findViewById(R.id.tabLayout);

        // Create two tabs
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        // Accessed from inner class, needs to be final
        // View, which handles fragments connected with tabs
        final ViewPager viewPager = (ViewPager) tabsView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(
                getFragmentManager(), tabLayout.getTabCount()
        ));

        // Assigns the ViewPager to TabLayout
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() >= 2) {
            // Setting the names of tabs
            tabLayout.getTabAt(0).setText(R.string.habit_tabs_general);
            tabLayout.getTabAt(1).setText(R.string.habit_tabs_statistics);
        }

        // Now when a user swipes ViewPager, TabLayout will update its indicator and selected tab;
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // And when a tab is selected, ViewPager will change to proper page.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // not implemented
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // not implemented
            }
        });


        return tabsView;
    }


    /**
     * Adapter for tabs (basically, the habit card and statistics)
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Fragment tab1 = new HabitCardFragment();
                    return tab1;
                case 1:
                    //TODO: Add statistics fragment
                    Fragment tab2 = new HabitStatisticsFragment();
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
}
