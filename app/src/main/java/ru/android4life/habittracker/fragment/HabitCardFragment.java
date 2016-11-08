package ru.android4life.habittracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.adapter.HabitParametersAdapter;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
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
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private int habitId;

    /*
            If Android decides to recreate your Fragment later, it's going to call the no-argument
        constructor of your fragment. So overloading the constructor is not a solution.
            With that being said, the way to pass stuff to your Fragment so that they are available
        after a Fragment is recreated by Android is to pass a bundle to the setArguments method.
        See https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
        for the details.
     */
    public static HabitCardFragment newInstance(int habitId) {

        Bundle args = new Bundle();

        HabitCardFragment fragment = new HabitCardFragment();
        // Setting an id of the clicked habit, see HabitListAdapter.onCreateViewHolder()
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
        // adapter for habits parameters (thank you, Bulat)
        mAdapter = new HabitParametersAdapter(getActivity(),
                HabitParameter.createParametersByHabitId(getContext(), habitId), false);

        MainActivity.toggle.setDrawerIndicatorEnabled(false);
        MainActivity.toggle.setHomeAsUpIndicator(R.drawable.ic_add_habit_back);
        MainActivity.toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                switch (MainActivity.drawerSelectionMode) {
                    case TODAY:
                        fragment = new HabitListFragment();
                        getActivity().setTitle(getString(R.string.today));
                        break;
                    case TOMORROW:
                        fragment = new HabitListFragment();
                        getActivity().setTitle(getString(R.string.tomorrow));
                        break;
                    case NEXT_MONTH:
                        fragment = new HabitListFragment();
                        getActivity().setTitle(getString(R.string.next_month));
                        break;
                    case ALL_TASKS:
                        fragment = new HabitsFragment();
                        getActivity().setTitle(getString(R.string.all_tasks));
                        break;
                }
                // Emptying fragments in the backstack
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Replacing activity content with the content of fragment
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, MainActivity.drawerSelectionMode.stringValue).commit();
                MainActivity.toggle.setDrawerIndicatorEnabled(true); //enable "hamburger" drawable
                MainActivity.toggle.syncState();
            }
        });
        MainActivity.toggle.syncState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View habitCard = inflater.inflate(R.layout.habit_card, container, false);

        //list of settings
        RecyclerView recyclerView = (RecyclerView) habitCard.findViewById(R.id.habit_settings);

        // Habits name and question
        AppCompatTextView habitNameTextView = (AppCompatTextView) habitCard.findViewById(R.id.habit_card_name);
        AppCompatTextView habitQuestionTextView = (AppCompatTextView) habitCard.findViewById(R.id.habit_card_question);

        Habit habit = (Habit) habitDAO.findById(habitId);

        habitNameTextView.setText(habit.getName());
        habitQuestionTextView.setText(BaseActivity.getContext().getString(R.string.did_i_question,
                habit.getQuestion()));
        //as we need a vertical list, the layout manager is vertical
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);

        // Edit habit floating action button
        FloatingActionButton editHabit = (FloatingActionButton) habitCard.findViewById(R.id.fab_edit);
        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                intent.putExtra(getString(R.string.habit_id), habitId);
                startActivity(intent);
            }
        });
        return habitCard;
    }
}
