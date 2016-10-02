package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import az.plainpie.PieView;
import ru.android4life.habittracker.R;

/**
 * This class represents a fragment of right tab,
 * which contains statistics of habit.
 *
 * Created by Nikolay Yushkevich on 2.10.16.
 */

public class HabitStatisticsFragment extends Fragment {

    private RelativeLayout view;

    // Pie View — https://github.com/zurche/plain-pie
    private PieView pieView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.habit_statistics, container, false);
        pieView = (PieView) view.findViewById(R.id.pie_view);
        return view;

    }
}
