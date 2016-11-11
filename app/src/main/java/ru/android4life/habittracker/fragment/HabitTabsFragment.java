package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.adapter.PagerAdapter;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;

/**
 * Tabs controller class.
 * Layout becomes visible,
 * when the card is opened from the list of habits.
 * <p>
 * Created by Nikolay Yushkevich on 27.09.16.
 */
public class HabitTabsFragment extends Fragment {

    private int habitId;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;

    /*
            If Android decides to recreate your Fragment later, it's going to call the no-argument
        constructor of your fragment. So overloading the constructor is not a solution.
            With that being said, the way to pass stuff to your Fragment so that they are available
        after a Fragment is recreated by Android is to pass a bundle to the setArguments method.
        See https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
        for the details.
     */
    public static HabitTabsFragment newInstance(int habitId) {

        Bundle args = new Bundle();

        HabitTabsFragment fragment = new HabitTabsFragment();
        // Setting an id of the clicked habitSchedule, see HabitListAdapter.onCreateViewHolder()
        args.putInt(BaseActivity.getContext().getString(R.string.habit_id), habitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habitId = this.getArguments().getInt(BaseActivity.getContext().getString(R.string.habit_id));
        habitScheduleDAO = new HabitScheduleDAO(BaseActivity.getContext());
        habitDAO = new HabitDAO(BaseActivity.getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View tabsView = inflater.inflate(R.layout.habit_tabs, container, false);

        // Strip with tabs, which goes under the action bar.
        TabLayout tabLayout = (TabLayout) tabsView.findViewById(R.id.tabLayout);

        // Create three tabs
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        // Accessed from inner class, needs to be final
        // View, which handles fragments connected with tabs
        final ViewPager viewPager = (ViewPager) tabsView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount(), habitId));

        // Assigns the ViewPager to TabLayout
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.habit_tabs_general);
        tabLayout.getTabAt(1).setText(R.string.habit_tabs_statistics);
        tabLayout.getTabAt(2).setText(R.string.habit_tabs_notes);

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

        Habit habit = (Habit) habitDAO.findById(habitId);
        getActivity().setTitle(habit.getName());

        return tabsView;
    }
}
