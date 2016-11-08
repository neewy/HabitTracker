package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import az.plainpie.PieView;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;

/**
 * This class represents a fragment of right tab,
 * which contains statistics of habit.
 * <p>
 * Created by Nikolay Yushkevich on 2.10.16.
 */

public class HabitStatisticsFragment extends Fragment {

    // Pie View — https://github.com/zurche/plain-pie
    private PieView pieView;

    /*
            If Android decides to recreate your Fragment later, it's going to call the no-argument
        constructor of your fragment. So overloading the constructor is not a solution.
            With that being said, the way to pass stuff to your Fragment so that they are available
        after a Fragment is recreated by Android is to pass a bundle to the setArguments method.
        See https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
        for the details.
     */
    public static HabitStatisticsFragment newInstance(int habitId) {

        Bundle args = new Bundle();

        HabitStatisticsFragment fragment = new HabitStatisticsFragment();
        // Setting an id of the clicked habit, see HabitListAdapter.onCreateViewHolder()
        args.putInt(BaseActivity.getContext().getString(R.string.habit_id), habitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.habit_statistics, container, false);
        pieView = (PieView) view.findViewById(R.id.pie_view);
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        pieView.setPercentageBackgroundColor(typedValue.data);

        int habitId = this.getArguments()
                .getInt(BaseActivity.getContext().getString(R.string.habit_id));
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(BaseActivity.getContext());
        double percentage = habitScheduleDAO
                .getPercentageOfDoneSchedulesForDistinctHabitByHabitId(habitId);

        if (percentage == 0) // Plain Pie Chart work inadequately with 0 values, pieChart becomes invisible
            percentage = 0.1;

        setPercentage(Float.parseFloat(String.valueOf(percentage)));
        return view;
    }

    private void setPercentage(Float percentage) {
        pieView.setmPercentage(percentage);
        pieView.setInnerText(String.valueOf(percentage.intValue()) + "%");
    }
}