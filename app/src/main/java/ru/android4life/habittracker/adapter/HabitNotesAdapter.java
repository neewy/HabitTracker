package ru.android4life.habittracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

import static ru.android4life.habittracker.activity.BaseActivity.getContext;


public class HabitNotesAdapter extends RecyclerView.Adapter<HabitNotesAdapter.ViewHolder> {

    private List<HabitSchedule> schedules;

    public HabitNotesAdapter(int habitId) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(getContext());
        List<HabitSchedule> schedules = habitScheduleDAO.findByHabitIdSortedByDateInDescendingOrder(habitId);
        this.schedules = new ArrayList<>();
        for (HabitSchedule schedule : schedules) {
            if (schedule.isDone() != null) {
                this.schedules.add(schedule);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_text_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", MainActivity.locale);
        String date = dateFormat.format(schedules.get(position).getDatetime());
        holder.date.setText(date);
        holder.name.setText(schedules.get(position).getNote());
        if (schedules.get(position).isDone()) {
            holder.name.setText(R.string.was_done);
        } else {
            holder.name.setText(R.string.was_skipped);
        }
        if (schedules.get(position).getNote() != null) {
            holder.name.setText(holder.name.getText() + " " + schedules.get(position).getNote());
        }

    }


    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView name;
        public RelativeLayout layout;

        ViewHolder(View v) {
            super(v);
            date = (TextView) itemView.findViewById(R.id.note_date);
            name = (TextView) itemView.findViewById(R.id.note_name);
            layout = (RelativeLayout) v;
        }
    }
}
