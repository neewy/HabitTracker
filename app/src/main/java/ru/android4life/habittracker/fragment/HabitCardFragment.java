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

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.adapter.HabitParametersAdapter;
import ru.android4life.habittracker.models.HabitParameter;

/**
 * This class represents a fragment of left tab,
 * which contains confirmation card of habit,
 * and list of habit settings as well.
 * <p>
 * Created by Nikolay Yushkevich on 27.09.16.
 */

public class HabitCardFragment extends Fragment {

    private HabitParametersAdapter mAdapter;

    /*
            If Android decides to recreate your Fragment later, it's going to call the no-argument
        constructor of your fragment. So overloading the constructor is not a solution.
            With that being said, the way to pass stuff to your Fragment so that they are available
        after a Fragment is recreated by Android is to pass a bundle to the setArguments method.
        See https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
        for the details.
     */
    public static HabitCardFragment newInstance(int habitScheduleId) {

        Bundle args = new Bundle();

        HabitCardFragment fragment = new HabitCardFragment();
        // Setting an id of the clicked habit, see HabitListAdapter.onCreateViewHolder()
        args.putInt(BaseActivity.getContext().getString(R.string.habit_schedule_id), habitScheduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int habitScheduleId = this.getArguments().getInt(BaseActivity.getContext().getString(R.string.habit_schedule_id));
        // adapter for habits parameters (thank you, Bulat)
        mAdapter = new HabitParametersAdapter(getActivity(),
                HabitParameter.createParametersByHabitScheduleId(getContext(), habitScheduleId), false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View habitCard = inflater.inflate(R.layout.habit_card, container, false);

        //list of settings
        RecyclerView recyclerView = (RecyclerView) habitCard.findViewById(R.id.habit_settings);

        //as we need a vertical list, the layout manager is vertical
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton editHabit = (FloatingActionButton) habitCard.findViewById(R.id.fab_edit);
        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });

        return habitCard;
    }

}
