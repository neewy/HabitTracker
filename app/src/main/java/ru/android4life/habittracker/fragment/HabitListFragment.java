package ru.android4life.habittracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.adapter.HabitListAdapter;
import ru.android4life.habittracker.database.Habit;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListFragment extends Fragment {

    private RelativeLayout view;
    private RecyclerView listView;
    private LinearLayoutManager mLinearLayoutManager;
    private HabitListAdapter mAdapter;
    private List<Habit> habits;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habits = createDummyListOfHabits();
        mAdapter = new HabitListAdapter(habits, getFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //creating view from layout, attachToRoot false — so the parent cannot listen to events of inflated view
        view = (RelativeLayout) inflater.inflate(R.layout.habit_list, container, false);
        listView = (RecyclerView) view.findViewById(R.id.habits_list);

        showHabits();

        // Process floating action bar
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    // creates some dummy objects, so the list appears in the view
    // tip: comment addition of habits to check empty list view
    private List<Habit> createDummyListOfHabits() {
        List<Habit> list = new ArrayList<>();
        list.add(new Habit());
        list.add(new Habit());
        list.add(new Habit());
        return list;
    }

    private void showHabits(){
        if (habits.isEmpty()) {
            ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.switcher);
            switcher.showNext();
        } else {
            setUpAndShowListOfHabits();
        }
    }

    private void setUpAndShowListOfHabits() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(mLinearLayoutManager);
        listView.setAdapter(mAdapter);
    }

}
