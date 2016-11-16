package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;


public class HabitNotesAdapter extends RecyclerView.Adapter<HabitNotesAdapter.ViewHolder> {

    private List<String> mDataset;

    public HabitNotesAdapter(int habitId, Context context) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        List<HabitSchedule> schedules = habitScheduleDAO.findByHabitId(habitId);
        mDataset = new ArrayList<>();
        for (HabitSchedule schedule : schedules) {
            mDataset.add(schedule.getNote());
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
        holder.mTextView.setText(mDataset.get(position));
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.note_name);
        }
    }
}
