package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;

/**
 * This class represents a fragment of right tab,
 * which contains statistics of habit.
 *
 * Created by Nikolay Yushkevich on 2.10.16.
 */

public class HabitStatisticsFragment extends Fragment {

    private RelativeLayout view;
    LineChart chart;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.habit_statistics, container, false);
        chart = (LineChart) view.findViewById(R.id.chart);
        float[][] data = new float[][]{{0,1,2,3,4,5,6,7,8},{1f/3f,2f/3f,1f,1f/5f,2f/6f,1f,1f,0f,2f/3f}};
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < data[0].length; i++) {
            entries.add(new Entry(data[0][i], data[1][i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setValueTextColor(R.color.colorAccent);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setDrawBorders(false);
        //chart.setDrawGridBackground(false);
        chart.setGridBackgroundColor(R.color.transparent);
        chart.invalidate();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart.invalidate();
    }
}
