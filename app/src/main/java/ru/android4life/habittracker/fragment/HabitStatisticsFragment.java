package ru.android4life.habittracker.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import ru.android4life.habittracker.HabitPerformanceData;
import ru.android4life.habittracker.R;

/**
 * This class represents a fragment of right tab,
 * which contains statistics of habit.
 *
 * Created by Nikolay Yushkevich on 2.10.16.
 */

public class HabitStatisticsFragment extends Fragment {

    private RelativeLayout view;
    private LineChartView chart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.habit_statistics, container, false);
        chart = (LineChartView) view.findViewById(R.id.chart);

        setGraphData(generateStatistics());
        setGraphSize();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Creates a line on a graph based on
     * the dates of habits and performance
     * @param habitStats
     */
    public void setGraphData(List<HabitPerformanceData> habitStats) {
        // formatter for only week day names
        DateFormat formatter = new SimpleDateFormat("EEEE", Locale.US);

        // list for values of ratio
        List<PointValue> values = new ArrayList<PointValue>();

        // list for string names of x axis
        List<String> xAxis = new ArrayList<String>();

        // stupid list for this library to work
        List<Float> xOrderAxis = new ArrayList<Float>();
        for (int i = 0; i < habitStats.size(); i++) {
            values.add(new PointValue(i, habitStats.get(i).getDoneRatio()));
            xAxis.add(formatter.format(habitStats.get(i).getDay()));
            xOrderAxis.add((float) i);
        }
        // setting up the line
        Line line = new Line(values).setColor(Color.parseColor("#02b9e6")).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chart.setInteractive(true);

        Axis axisX = Axis.generateAxisFromCollection(xOrderAxis, xAxis);
        Axis axisY = new Axis();
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setLineChartData(data);
    }

    // Setting up the size of the graph to fit into the screen
    public void setGraphSize() {
        chart.setViewportCalculationEnabled(true);
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom -= 0.1;
        v.top += 0.1;
        v.left -= 0.5;
        v.right += 0.5;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    // generates list of dummy data
    private List<HabitPerformanceData> generateStatistics() {
        List<HabitPerformanceData> stats = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        stats.add(new HabitPerformanceData(cal.getTime(), 3, 7));
        cal.add(Calendar.DATE, 2);
        stats.add(new HabitPerformanceData(cal.getTime(), 1, 7));
        cal.add(Calendar.DATE, 1);
        stats.add(new HabitPerformanceData(cal.getTime(), 3, 3));
        cal.add(Calendar.DATE, 1);
        stats.add(new HabitPerformanceData(cal.getTime(), 5, 7));
        return stats;
    }
}
