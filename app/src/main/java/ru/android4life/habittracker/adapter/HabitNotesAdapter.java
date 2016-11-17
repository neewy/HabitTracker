package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.utils.StringConstants;


public class HabitNotesAdapter extends RecyclerView.Adapter<HabitNotesAdapter.ViewHolder> {

    private List<String> mDataset;

    public HabitNotesAdapter(int habitId, Context context) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        List<HabitSchedule> schedules = habitScheduleDAO.findByHabitId(habitId);
        mDataset = new ArrayList<>();
        SimpleDateFormat dateFormatDaysAndMonthNumbers =
                new SimpleDateFormat("dd.MM", Locale.ENGLISH);
        for (HabitSchedule schedule : schedules) {
            Date currentDate = new Date();
            if(schedule.getDatetime().before(currentDate)) {
                String historyNote = context.getString(R.string.two_subsequent_strings,
                        dateFormatDaysAndMonthNumbers.format(schedule.getDatetime()),
                        StringConstants.SPACE);
                if (schedule.isDone() != null && schedule.isDone() && schedule.getNote() != null &&
                        !schedule.getNote().equals(StringConstants.EMPTY_STRING))
                    historyNote += context.getString(R.string.habit_was_performed_and_note,
                            schedule.getNote());
                else if (schedule.isDone() != null && schedule.isDone())
                    historyNote += context.getString(R.string.habit_was_performed);
                else
                    historyNote += context.getString(R.string.habit_was_skipped);

                mDataset.add(historyNote);
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
