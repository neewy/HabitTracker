package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.adapter.HabitNotesAdapter;

/**
 * Created by Bulat Mukhutdinov on 11.11.16.
 */

public class HabitNotesFragment extends Fragment {
    private HabitNotesAdapter mAdapter;
    private int habitId;

    public static HabitNotesFragment newInstance(int habitId) {
        Bundle args = new Bundle();
        args.putInt(BaseActivity.getContext().getString(R.string.habit_id), habitId);
        HabitNotesFragment fragment = new HabitNotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        habitId = this.getArguments()
                .getInt(BaseActivity.getContext().getString(R.string.habit_id));
        mAdapter = new HabitNotesAdapter(habitId, BaseActivity.getContext());
        ((MainActivity) getActivity()).foo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.habit_notes, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.habit_notes_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(mLinearLayoutManager);
        listView.setAdapter(mAdapter);

        return view;
    }
}