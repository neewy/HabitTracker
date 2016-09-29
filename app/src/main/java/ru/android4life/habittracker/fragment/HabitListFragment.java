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

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.adapter.HabitListAdapter;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListFragment extends Fragment {

    private RelativeLayout view;
    private RecyclerView listView;
    private LinearLayoutManager mLinearLayoutManager;
    private HabitListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HabitListAdapter(getFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //creating view from layout, attachToRoot false — so the parent cannot listen to events of inflated view
        view = (RelativeLayout) inflater.inflate(R.layout.habit_list, container, false);
        listView = (RecyclerView) view.findViewById(R.id.habits_list);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Process floating action bar
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });

        listView.setHasFixedSize(true);
        listView.setLayoutManager(mLinearLayoutManager);
        listView.setAdapter(mAdapter);
        return view;
    }

}
