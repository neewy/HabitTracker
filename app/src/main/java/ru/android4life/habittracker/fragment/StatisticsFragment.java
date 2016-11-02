package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

import static ru.android4life.habittracker.utils.StringConstants.DAY_IN_MS;

/**
 * Created by Bulat Mukhutdinov on 06.10.2016.
 */

/**
 * Fragment of general statistics
 */
public class StatisticsFragment extends Fragment {
    private LineChartView chart;
    private LineChartData data;
    /**
     * Deep copy of data.
     */
    private LineChartData previewData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        chart = (LineChartView) rootView.findViewById(R.id.general_statistics_chart);
        PreviewLineChartView previewChart = (PreviewLineChartView) rootView.findViewById(R.id.general_statistics_preview);

        // Generate data for previewed chart and copy of that data for preview chart.
        generateData();

        chart.setLineChartData(data);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);
        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportListener());

        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dx = tempViewport.width() / 3; // bigger the divider bigger preview window
        tempViewport.inset(dx, 0); // set width and height of preview window
        previewChart.setCurrentViewportWithAnimation(tempViewport);
        previewChart.setZoomType(ZoomType.HORIZONTAL);

        return rootView;
    }

    private void generateData() {
        Calendar c = Calendar.getInstance();
        // Get how many days in current month
        int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<PointValue> values = new ArrayList<>();
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(MainActivity.getContext());
        c = new GregorianCalendar();
        // Get how many days in current month
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date habitDay = c.getTime();
        c.add(Calendar.MONTH, -1);
        c.add(Calendar.DATE, 1);
        Date date = c.getTime();
        List<HabitSchedule> habitSchedules = new ArrayList<>(habitScheduleDAO
                .findInRange(new Date(habitDay.getTime() - (monthMaxDays * DAY_IN_MS)), new Date()));
        int skipped, performed;
        int pointsOrder = 0; // order in which point added to the graph
        SimpleDateFormat dateFormatDayOfMonthNumber =
                new SimpleDateFormat("d", Locale.ENGLISH);
        while (date.before(habitDay) || date.equals(habitDay)) {
            skipped = 0;
            performed = 0;
            for (HabitSchedule habitSchedule : habitSchedules) {
                if (dateFormatDayOfMonthNumber.format(habitSchedule.getDatetime())
                        .equals(dateFormatDayOfMonthNumber.format(date))) {
                    if (habitSchedule.isDone() != null && habitSchedule.isDone()) {
                        performed++;
                    } else {
                        skipped++;
                    }
                }
            }
            float ratio = performed == 0 && skipped == 0 ? 0 : (float) performed / (skipped + performed);
            PointValue pv = new PointValue(pointsOrder, ratio);

            pv = pv.setLabel(dateFormatDayOfMonthNumber.format(date));
            values.add(pv);
            c.add(Calendar.DATE, 1);
            date = c.getTime();
            pointsOrder++;
        }

        Line line = new Line(values);
        line = line.setHasLabels(true); // for displaying labels on the graph
        line.setColor(MainActivity.getContext().getResources().getColor(R.color.colorPrimary));
        line.setHasPoints(false); // too many values so don't draw points.
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        // Workaround to set Y axis max value to 1
        List<PointValue> v = new ArrayList<>();
        v.add(new PointValue(1, 1.05f));
        Line l = new Line(v);
        l.setPointRadius(0);
        lines.add(l);


        data = new LineChartData(lines);
        Axis axisX = new Axis();
        axisX.setName(MainActivity.getContext().getResources().getString(R.string.days));

        // for displaying labels on the graph
        List<AxisValue> axisValues = new ArrayList<>();
        AxisValue axisValue;
        for (int j = 0; j < values.size(); j++) {
            axisValue = new AxisValue(values.get(j).getX(), values.get(j).getLabelAsChars());
            axisValues.add(axisValue);
        }
        axisX.setValues(axisValues);

        data.setAxisXBottom(axisX);
        Axis axisY = new Axis();
        axisY.setName(MainActivity.getContext().getResources().getString(R.string.productivity));
        axisY.setHasLines(true);

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        // prepare preview data, is better to use separate deep copy for preview chart.
        // Set color to grey to make preview area more visible.
        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
    }


    /**
     * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
     * viewport of upper chart.
     */
    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chart.setCurrentViewport(newViewport);
        }
    }
}
