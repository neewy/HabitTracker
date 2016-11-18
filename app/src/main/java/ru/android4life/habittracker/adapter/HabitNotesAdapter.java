package ru.android4life.habittracker.adapter;

import android.content.Context;
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


public class HabitNotesAdapter extends RecyclerView.Adapter<HabitNotesAdapter.ViewHolder> {

    private List<HabitSchedule> mDataset;
    private Context context;

    public HabitNotesAdapter(int habitId, Context context) {
        this.context = context;
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        List<HabitSchedule> schedules = habitScheduleDAO.findByHabitIdSortedByDateInDescendingOrder(habitId);
        mDataset = new ArrayList<>();
        for (HabitSchedule schedule : schedules) {
            if (schedule.isDone() != null && !schedule.getNote().isEmpty()) {
                mDataset.add(schedule);
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
        String date = dateFormat.format(mDataset.get(position).getDatetime());
        holder.date.setText(date);
        holder.name.setText(mDataset.get(position).getNote());
        if (mDataset.get(position).isDone()) {
            holder.name.setText(R.string.was_done);
        } else {
            holder.name.setText(R.string.was_skipped);
        }
        holder.name.setText(holder.name.getText() + " " +  mDataset.get(position).getNote());
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView name;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            date = (TextView) itemView.findViewById(R.id.note_date);
            name = (TextView) itemView.findViewById(R.id.note_name);
            layout = (RelativeLayout) v;
        }
    }
}
